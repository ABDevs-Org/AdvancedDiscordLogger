package org.abdevs.advanceddiscordlogger.api;

import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import net.dv8tion.jda.api.JDA;
import org.abdevs.advanceddiscordlogger.enities.Extension;
import org.abdevs.advanceddiscordlogger.utils.Constants;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AdvancedDiscordLoggerAPI {

    private AdvancedDiscordLoggerAPI() {
    }

    /**
     * List of all enabled {@link Extension}
     */
    public static List<Extension> getEnabledExtensions() {
        return Constants.enabledExtensions;
    }

    /**
     * JDA instance, null if JDA is not ready.
     * check availability with {@link #isJDAReady()}
     */
    @Nullable
    public static JDA getJda() {
        return Constants.jda;
    }

    /**
     * All the registered {@link Listener} by {@link Extension}.
     */
    public static ConcurrentHashMap<Extension, List<Listener>> getExtensionListeners() {
        return Constants.extensionListeners;
    }

    /**
     * All the registered {@link CommandBase} by {@link Extension}.
     */
    public static ConcurrentHashMap<Extension, List<CommandBase>> getExtensionCommands() {
        return Constants.extensionCommands;
    }

    /**
     * Instance of {@link CommandManager} which handles all
     * all the plugin commands and extension commands
     */
    public static CommandManager getCommandManager() {
        return Constants.commandManager;
    }

    /**
     * @return if the JDA instance is ready to be used.
     */
    public static boolean isJDAReady() {
        return Constants.jda != null && Constants.jda.getStatus().equals(JDA.Status.CONNECTED);
    }

    /**
     * @param extensionName Name of the extension
     * @return {@link Extension} object of the extension
     * or {@null} if no extension found with that name
     */
    @Nullable
    public static Extension getExtension(String extensionName) {
        return Constants.enabledExtensions.stream().filter(extension -> extension.getName().equals(extensionName)).findFirst().orElse(null);
    }
}
