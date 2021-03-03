package org.abdevs.advanceddiscordlogger.api.utils;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import me.mattstudios.mf.base.CommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.abdevs.advanceddiscordlogger.enities.LogInfoData;
import org.abdevs.advanceddiscordlogger.enities.LogLevel;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public interface ExtensionUtils {
    @Nullable FileConfiguration createOrFetchConfig(@NotNull String folderName, @NotNull Class<?> clazz, @NotNull String fileName);

    void registerListeners(@NotNull ExtensionData extension, @NotNull Listener... listeners);

    void registerCommands(@NotNull ExtensionData extension, @NotNull CommandBase... commands);

    @NotNull String colorize(@NotNull String message);

    @Deprecated
    void eLog(@NotNull String message, @NotNull ExtensionData extension);

    void eDiscordLog(@NotNull String message, @NotNull LogLevel level, @NotNull ExtensionData extension);

    @Nullable EmbedBuilder embedFromSection(@NotNull ConfigurationSection config);

    @Nullable EmbedBuilder embedFromSection(@NotNull ConfigurationSection config, @Nullable OfflinePlayer player);

    @Nullable String setPlaceholders(@Nullable OfflinePlayer player, @Nullable String message);

    @Nullable String emptyStringToNull(@Nullable String value);

    @NotNull LogInfoData logInfoFromSection(@NotNull ConfigurationSection config);

    void executeCommands(@NotNull List<String> commands);

    void executeCommands(@NotNull List<String> commands, @Nullable OfflinePlayer player);

    void sendMessage(@Nullable String message, @Nullable EmbedBuilder embedBuilder, @NotNull LogInfoData infoData, @NotNull JDAWebhookClient client, @Nullable Consumer<ReadonlyMessage> response);


    void sendMessage(@Nullable String message, @Nullable EmbedBuilder embedBuilder, @NotNull LogInfoData infoData, @Nullable Consumer<Message> response);

    void initiateBaseLogging(@NotNull ConfigurationSection config, @Nullable JDAWebhookClient client, @Nullable OfflinePlayer player);
}
