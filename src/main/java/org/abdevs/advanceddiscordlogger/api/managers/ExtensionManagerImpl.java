package org.abdevs.advanceddiscordlogger.api.managers;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.mattstudios.mf.base.CommandBase;
import org.abdevs.advanceddiscordlogger.AdvancedDiscordLogger;
import org.abdevs.advanceddiscordlogger.api.base.Extension;
import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.abdevs.advanceddiscordlogger.enities.Logger;
import org.abdevs.advanceddiscordlogger.utils.Utils;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ExtensionManagerImpl implements ExtensionManager {


    @Override
    public void loadExtensionsAsync(@NotNull("Extensions folder can't be null") File extensionsFolder) {
        CompletableFuture.runAsync(() -> loadExtensions(extensionsFolder));
    }


    @Override
    public void loadExtensions(@NotNull("Extensions folder can't be null") File extensionsFolder) {
        if (extensionsFolder.mkdir()) Utils.log("&c&lExtensions directory created successfully");
        final FileFilter jarFileFilter = pathname -> pathname.isFile() && pathname.getName().endsWith(".jar");
        final List<File> files = Arrays.stream(extensionsFolder.listFiles(jarFileFilter)).collect(Collectors.toList());
        files.forEach(this::loadExtension);
    }


    @Override
    public void loadExtensionAsync(@NotNull("Extension file name can't be null") String fileName) {
        CompletableFuture.runAsync(() -> loadExtension(fileName));
    }


    @Override
    public void loadExtension(@NotNull("Extension file name can't be null") String fileName) {
        final File extensionsFolder = new File(AdvancedDiscordLogger.getPlugin().getDataFolder(), "extensions");
        final FileFilter filter = pathname -> pathname.getName().equals(fileName) && pathname.getName().endsWith(".jar") && pathname.isFile();
        final List<File> files = Arrays.stream(extensionsFolder.listFiles(filter)).collect(Collectors.toList());
        if (files.isEmpty()) throw new IllegalArgumentException("No extension file found with the name " + fileName);
        final File file = files.get(0);
        loadExtension(file);
    }


    @Override
    public void loadExtensionAsync(@NotNull("Extension File can't be null") File jarFile) {
        CompletableFuture.runAsync(() -> loadExtension(jarFile));
    }


    @Override
    public void loadExtension(@NotNull("Extension File can't be null") File jarFile) {
        String extName = null;
        final AdvancedDiscordLogger plugin = AdvancedDiscordLogger.getPlugin();
        final List<ExtensionData> enabledExtensionData = plugin.getEnabledExtensionData();
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
            final List<String> extensionsByName = AdvancedDiscordLogger.getPlugin().getEnabledExtensionData().stream().map(ExtensionData::getName).collect(Collectors.toList());
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
            if (!(extensionInstance instanceof Extension))
                throw new IllegalStateException(jarFile.getName() + " is not implementing " + Extension.class.getPackage().getName() + "." + Extension.class.getName() + " interface");
            final Extension instance = (Extension) extensionInstance;
            final ExtensionData extensionDataInfo = new ExtensionData(extensionName, extensionVersion, extensionDescription, extensionAuthor, instance, jarFile, configObject);
            final Logger logger = new Logger(extensionDataInfo);
            final Method initMethod = Extension.class.getDeclaredMethod("init", ExtensionData.class, Logger.class, ClassLoader.class);
            initMethod.setAccessible(true);
            initMethod.invoke(instance, extensionDataInfo, logger, instance.getClass().getClassLoader());
            initMethod.setAccessible(false);
            enabledExtensionData.add(extensionDataInfo);
            extName = extensionName;
            instance.onEnable();
        } catch (Exception e) {
            if (extName != null) {
                final ExtensionData extensionData = AdvancedDiscordLogger.getApi().getExtension(extName);
                if (extensionData != null) {
                    extensionData.getInstance().onDisable();
                    enabledExtensionData.remove(extensionData);
                }
            }
            Utils.log("&c&lSomething went wrong while enabling " + jarFile.getName() + " extension.");
            e.printStackTrace();
        }
    }


    @Override
    public void unloadAllExtensionsAsync() {
        CompletableFuture.runAsync(this::unloadAllExtensions);
    }


    @Override
    public void unloadAllExtensions() {
        final List<ExtensionData> enabledExtensionData = new ArrayList<>(AdvancedDiscordLogger.getPlugin().getEnabledExtensionData());
        enabledExtensionData.forEach(this::unloadExtension);
    }


    @Override
    public void unloadExtensionAsync(@NotNull("Extension File can't be null") File jarFile) {
        CompletableFuture.runAsync(() -> unloadExtension(jarFile));
    }


    @Override
    public void unloadExtension(@NotNull("Extension File can't be null") File jarFile) {
        final List<ExtensionData> enabledExtensionData = AdvancedDiscordLogger.getPlugin().getEnabledExtensionData();
        final List<File> extensionsByFile = enabledExtensionData.stream().map(ExtensionData::getJarFile).collect(Collectors.toList());
        if (!extensionsByFile.contains(jarFile))
            throw new IllegalArgumentException("Extension in file " + jarFile.getName() + " isn't active.");
        final ExtensionData extInfo = enabledExtensionData.stream().filter(extension -> extension.getJarFile().equals(jarFile)).collect(Collectors.toList()).get(0);
        unloadExtension(extInfo);
    }


    @Override
    public void unloadExtensionAsync(@NotNull("Extension name can't be null") String extensionName) {
        CompletableFuture.runAsync(() -> unloadExtension(extensionName));
    }


    @Override
    public void unloadExtension(@NotNull("Extension name can't be null") String extensionName) {
        final ExtensionData extensionData = AdvancedDiscordLogger.getApi().getExtension(extensionName);
        if (extensionData == null)
            throw new IllegalArgumentException("Extension with name " + extensionName + " isn't active or does not exists.");
        unloadExtension(extensionData);
    }


    @Override
    public void unloadExtensionAsync(@NotNull("Extension can't be null") ExtensionData extensionData) {
        CompletableFuture.runAsync(() -> unloadExtension(extensionData));
    }


    @Override
    public void unloadExtension(@NotNull ExtensionData extensionData) {
        Utils.log("&e&lDisabling " + extensionData.getName() + " extension...");
        final AdvancedDiscordLogger plugin = AdvancedDiscordLogger.getPlugin();
        final ConcurrentHashMap<ExtensionData, List<Listener>> extensionListeners = plugin.getExtensionListeners();
        final ConcurrentHashMap<ExtensionData, List<CommandBase>> extensionCommands = plugin.getExtensionCommands();
        final ConcurrentHashMap<ExtensionData, List<JDAWebhookClient>> extensionWebhooks = plugin.getExtensionWebhooks();
        final List<Listener> listeners = extensionListeners.get(extensionData);
        final List<CommandBase> commands = extensionCommands.get(extensionData);
        final List<JDAWebhookClient> jdaWebhookClients = extensionWebhooks.get(extensionData);
        final List<ExtensionData> enabledExtensionData = plugin.getEnabledExtensionData();
        if (listeners != null) {
            listeners.forEach(HandlerList::unregisterAll);
            extensionListeners.remove(extensionData);
        }
        if (commands != null) {
            commands.forEach(Utils::unRegisterCommand);
            extensionCommands.remove(extensionData);
        }
        if (jdaWebhookClients != null) {
            jdaWebhookClients.forEach(client -> {
                if (!client.isShutdown()) client.close();
            });
            extensionWebhooks.remove(extensionData);
        }
        extensionData.getInstance().onDisable();
        enabledExtensionData.remove(extensionData);
        Utils.log("&a&lSuccessfully disabled " + extensionData.getName() + " extension");
    }
}
