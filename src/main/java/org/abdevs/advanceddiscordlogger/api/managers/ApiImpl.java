package org.abdevs.advanceddiscordlogger.api.managers;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import net.dv8tion.jda.api.JDA;
import org.abdevs.advanceddiscordlogger.AdvancedDiscordLogger;
import org.abdevs.advanceddiscordlogger.api.utils.ExtensionUtils;
import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ApiImpl implements Api {

    @Override
    public @NotNull List<ExtensionData> getEnabledExtensions() {
        return AdvancedDiscordLogger.getPlugin().getEnabledExtensionData();
    }

    @Nullable
    @Override
    public JDA getJDA() {
        return AdvancedDiscordLogger.getPlugin().getJda();
    }

    @Override
    public @NotNull ConcurrentHashMap<ExtensionData, List<Listener>> getExtensionListeners() {
        return AdvancedDiscordLogger.getPlugin().getExtensionListeners();
    }

    @Override
    public @NotNull ConcurrentHashMap<ExtensionData, List<CommandBase>> getExtensionCommands() {
        return AdvancedDiscordLogger.getPlugin().getExtensionCommands();
    }

    @Override
    public @NotNull ConcurrentHashMap<ExtensionData, List<JDAWebhookClient>> getExtensionWebhooks() {
        return AdvancedDiscordLogger.getPlugin().getExtensionWebhooks();
    }

    @Override
    public @NotNull CommandManager getCommandManager() {
        return AdvancedDiscordLogger.getPlugin().getCommandManager();
    }

    @Override
    public boolean isJDAReady() {
        final AdvancedDiscordLogger plugin = AdvancedDiscordLogger.getPlugin();
        return plugin.getJda() != null && plugin.getJda().getStatus().equals(JDA.Status.CONNECTED);
    }

    @Nullable
    @Override
    public ExtensionData getExtension(String extensionName) {
        return getEnabledExtensions().stream().filter(extension -> extension.getName().equals(extensionName)).findFirst().orElse(null);
    }

    @Override
    public @NotNull ExtensionUtils getExtensionUtils() {
        return AdvancedDiscordLogger.getPlugin().getExtensionUtils();
    }

    @Override
    public @NotNull ExtensionManager getExtensionManager() {
        return AdvancedDiscordLogger.getPlugin().getExtensionManager();
    }

    @Override
    public @NotNull WebhookManager getWebhookManager() {
        return AdvancedDiscordLogger.getPlugin().getWebhookManager();
    }
}
