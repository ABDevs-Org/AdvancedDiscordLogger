package org.abdevs.advanceddiscordlogger.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.mattstudios.mf.base.CommandBase;
import org.abdevs.advanceddiscordlogger.AdvancedDiscordLogger;
import org.abdevs.advanceddiscordlogger.api.AdvancedDiscordLoggerAPI;
import org.abdevs.advanceddiscordlogger.api.IExtension;
import org.abdevs.advanceddiscordlogger.enities.Extension;
import org.abdevs.advanceddiscordlogger.utils.Constants;
import org.abdevs.advanceddiscordlogger.utils.Utils;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * The type Extension manager.
 */
@SuppressWarnings("unused")
public class ExtensionManager {

    private ExtensionManager() {
    }

    /**
     * Load extensions in folder async.
     *
     * @param extensionsFolder the extensions folder
     */
    public static void loadExtensionsAsync(@NotNull("Extensions folder can't be null") File extensionsFolder) {
        CompletableFuture.runAsync(() -> loadExtensions(extensionsFolder));
    }

    /**
     * Load extensions in folder.
     *
     * @param extensionsFolder the extensions folder
     */
    public static void loadExtensions(@NotNull("Extensions folder can't be null") File extensionsFolder) {
        if (extensionsFolder.mkdir()) Utils.log("&c&lExtensions directory created successfully");
        final FileFilter jarFileFilter = pathname -> pathname.isFile() && pathname.getName().endsWith(".jar");
        final List<File> files = Arrays.stream(extensionsFolder.listFiles(jarFileFilter)).collect(Collectors.toList());
        files.forEach(ExtensionManager::loadExtension);
    }

    /**
     * Load extension async.
     *
     * @param fileName the file name
     */
    public static void loadExtensionAsync(@NotNull("Extension file name can't be null") String fileName) {
        CompletableFuture.runAsync(() -> loadExtension(fileName));
    }

    /**
     * Load extension.
     *
     * @param fileName the file name
     */
    public static void loadExtension(@NotNull("Extension file name can't be null") String fileName) {
        final File extensionsFolder = new File(AdvancedDiscordLogger.getPlugin().getDataFolder(), "extensions");
        final FileFilter filter = pathname -> pathname.getName().equals(fileName) && pathname.getName().endsWith(".jar") && pathname.isFile();
        final List<File> files = Arrays.stream(extensionsFolder.listFiles(filter)).collect(Collectors.toList());
        if (files.isEmpty()) throw new IllegalArgumentException("No extension file found with the name " + fileName);
        final File file = files.get(0);
        loadExtension(file);
    }

    /**
     * Load extension async.
     *
     * @param jarFile the jar file
     */
    public static void loadExtensionAsync(@NotNull("Extension File can't be null") File jarFile) {
        CompletableFuture.runAsync(() -> loadExtension(jarFile));
    }

    /**
     * Load extension.
     *
     * @param jarFile the jar file
     */
    public static void loadExtension(@NotNull("Extension File can't be null") File jarFile) {
        String extName = null;
        final AdvancedDiscordLogger plugin = AdvancedDiscordLogger.getPlugin();
        try {
            final URL jarConfig;
            try {
                jarConfig = new URL("jar:file:" + jarFile.getAbsoluteFile() + "!/main.json");
            } catch (MalformedURLException e) {
                Utils.log("&cFailed to load extension. No main.json found in " + jarFile.getName());
                return;
            }
            final BufferedReader reader = new BufferedReader(new InputStreamReader(jarConfig.openStream()));
            final StringBuilder configBuilder = new StringBuilder();
            reader.lines().forEach(configBuilder::append);
            final JsonObject configObject = JsonParser.parseString(configBuilder.toString()).getAsJsonObject();
            final List<String> extensionsByName = AdvancedDiscordLoggerAPI.getEnabledExtensions().stream().map(Extension::getName).collect(Collectors.toList());
            final String extensionName = configObject.get("name").getAsString();
            if (extensionsByName.contains(extensionName))
                throw new IllegalStateException("The extension file " + jarFile.getName() + " is trying to register with the name " + extensionName + " which is already registered.");
            final String extensionVersion = configObject.get("version").getAsString();
            final String extensionDescription = configObject.get("description").getAsString();
            final String extensionAuthor = configObject.get("author").getAsString();
            final String extensionMainClassPath = configObject.get("main").getAsString();
            Utils.log("&e&lEnabling " + extensionName + " extension...");
            final URLClassLoader childClassLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, plugin.getClass().getClassLoader());
            final Class<?> extensionMainClass = childClassLoader.loadClass(extensionMainClassPath);
            final Object extensionInstance = extensionMainClass.newInstance();
            if (!(extensionInstance instanceof IExtension))
                throw new IllegalStateException(jarFile.getName() + " is not implementing " + IExtension.class.getPackage().getName() + "." + IExtension.class.getName() + " interface");
            final IExtension instance = (IExtension) extensionInstance;
            final Extension extensionInfo = new Extension(extensionName, extensionVersion, extensionDescription, extensionAuthor, instance, jarFile, configObject);
            Constants.enabledExtensions.add(extensionInfo);
            extName = extensionName;
            instance.onEnable(plugin);
            Utils.log("&a&lSuccessfully enabled " + extensionName + " extension");
        } catch (Exception e) {
            if (extName != null) {
                final Extension extension = AdvancedDiscordLoggerAPI.getExtension(extName);
                if (extension != null) {
                    extension.getInstance().onDisable(plugin);
                    Constants.enabledExtensions.remove(extension);
                }
            }
            Utils.log("&c&lSomething went wrong while enabling " + jarFile.getName() + " extension.");
            e.printStackTrace();
        }
    }

    /**
     * Un load all extensions async.
     */
    public static void unLoadAllExtensionsAsync() {
        CompletableFuture.runAsync(ExtensionManager::unLoadAllExtensions);
    }

    /**
     * Un load all extensions.
     */
    public static void unLoadAllExtensions() {
        final List<Extension> enabledExtensions = new ArrayList<>(AdvancedDiscordLoggerAPI.getEnabledExtensions());
        enabledExtensions.forEach(ExtensionManager::unLoadExtension);
    }

    /**
     * Un load extension async.
     *
     * @param jarFile the jar file
     */
    public static void unLoadExtensionAsync(@NotNull("Extension File can't be null") File jarFile) {
        CompletableFuture.runAsync(() -> unLoadExtension(jarFile));
    }

    /**
     * Un load extension.
     *
     * @param jarFile the jar file
     */
    public static void unLoadExtension(@NotNull("Extension File can't be null") File jarFile) {
        final List<Extension> enabledExtensions = AdvancedDiscordLoggerAPI.getEnabledExtensions();
        final List<File> extensionsByFile = enabledExtensions.stream().map(Extension::getJarFile).collect(Collectors.toList());
        if (!extensionsByFile.contains(jarFile))
            throw new IllegalArgumentException("Extension in file " + jarFile.getName() + " isn't active.");
        final Extension extInfo = enabledExtensions.stream().filter(extension -> extension.getJarFile().equals(jarFile)).collect(Collectors.toList()).get(0);
        unLoadExtension(extInfo);
    }

    /**
     * Un load extension async.
     *
     * @param extensionName the extension name
     */
    public static void unLoadExtensionAsync(@NotNull("Extension name can't be null") String extensionName) {
        CompletableFuture.runAsync(() -> unLoadExtension(extensionName));
    }

    /**
     * Un load extension.
     *
     * @param extensionName the extension name
     */
    public static void unLoadExtension(@NotNull("Extension name can't be null") String extensionName) {
        final Extension extension = AdvancedDiscordLoggerAPI.getExtension(extensionName);
        if (extension == null)
            throw new IllegalArgumentException("Extension with name " + extensionName + " isn't active or does not exists.");
        unLoadExtension(extension);
    }

    /**
     * Un load extension async.
     *
     * @param extension the extension
     */
    public static void unLoadExtensionAsync(@NotNull("Extension can't be null") Extension extension) {
        CompletableFuture.runAsync(() -> unLoadExtension(extension));
    }

    /**
     * Un load extension.
     *
     * @param extension the extension
     */
    public static void unLoadExtension(@NotNull Extension extension) {
        Utils.log("&e&lDisabling " + extension.getName() + " extension...");
        final List<Listener> listeners = AdvancedDiscordLoggerAPI.getExtensionListeners().get(extension);
        final List<CommandBase> commands = AdvancedDiscordLoggerAPI.getExtensionCommands().get(extension);
        if (listeners != null) {
            listeners.forEach(HandlerList::unregisterAll);
            Constants.extensionListeners.remove(extension);
        }
        if (commands != null) {
            commands.forEach(Utils::unRegisterCommand);
            Constants.extensionCommands.remove(extension);
        }
        extension.getInstance().onDisable(AdvancedDiscordLogger.getPlugin());
        Constants.enabledExtensions.remove(extension);
        Utils.log("&a&lSuccessfully disabled " + extension.getName() + " extension");
    }
}
