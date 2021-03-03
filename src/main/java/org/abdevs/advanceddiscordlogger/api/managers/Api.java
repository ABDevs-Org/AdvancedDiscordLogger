package org.abdevs.advanceddiscordlogger.api.managers;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import net.dv8tion.jda.api.JDA;
import org.abdevs.advanceddiscordlogger.api.utils.ExtensionUtils;
import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface Api {

    /**
     * @return List of all enabled {@link ExtensionData}
     */
    @NotNull
    List<ExtensionData> getEnabledExtensions();

    /**
     * @return JDA instance, null if JDA is not ready.
     * check availability with {@link #isJDAReady()}
     */
    @Nullable
    JDA getJDA();

    /**
     * @return All the registered {@link Listener} by {@link ExtensionData}.
     */
    @NotNull
    ConcurrentHashMap<ExtensionData, List<Listener>> getExtensionListeners();

    /**
     * @return All the registered {@link CommandBase} by {@link ExtensionData}.
     */
    @NotNull
    ConcurrentHashMap<ExtensionData, List<CommandBase>> getExtensionCommands();

    @NotNull
    ConcurrentHashMap<ExtensionData, List<JDAWebhookClient>> getExtensionWebhooks();

    /**
     * @return Instance of {@link CommandManager} which handles all
     * all the plugin commands and extension commands
     */
    @NotNull
    CommandManager getCommandManager();

    /**
     * @return true if the JDA instance is ready to be used.
     */
    boolean isJDAReady();

    /**
     * @param extensionName Name of the extension
     * @return {@link ExtensionData} object of the extension
     * or null if no extension found with that name
     */
    @Nullable
    ExtensionData getExtension(String extensionName);

    @NotNull
    ExtensionUtils getExtensionUtils();

    @NotNull
    ExtensionManager getExtensionManager();

    @NotNull
    WebhookManager getWebhookManager();
}
