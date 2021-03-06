package org.abdevs.advanceddiscordlogger.api.utils;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import me.mattstudios.mf.base.CommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.abdevs.advanceddiscordlogger.api.base.Extension;
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
    /**
     * Create or fetch config file configuration.
     *
     * @param folderName Name of the extension folder
     * @param clazz      Main class of the extension
     * @param fileName   Name of the resource file
     * @return configuration file configuration
     */
    @Nullable FileConfiguration createOrFetchConfig(@NotNull String folderName, @NotNull Class<?> clazz, @NotNull String fileName);

    /**
     * Register listeners.
     *
     * @param extension extension.
     * @param listeners listeners to register.
     * @throws IllegalArgumentException if no extension with name extensionName is active or exists.
     */
    void registerListeners(@NotNull ExtensionData extension, @NotNull Listener... listeners);

    /**
     * Register commands.
     *
     * @param extension extension.
     * @param commands  commands to register.
     * @throws IllegalArgumentException if no extension with name extensionName is active or exists.
     */
    void registerCommands(@NotNull ExtensionData extension, @NotNull CommandBase... commands);

    /**
     * Colorize string.
     *
     * @param message string message to be parsed
     * @return message with color codes
     */
    @NotNull String colorize(@NotNull String message);

    /**
     * E log.
     *
     * @param message   string message to print on the console
     * @param extension extension.
     * @throws IllegalArgumentException if no extension with name extensionName is active or exists.
     * @deprecated Use {@link Extension#getLogger()}
     */
    @Deprecated
    void eLog(@NotNull String message, @NotNull ExtensionData extension);

    /**
     * Sends log message to discord.
     *
     * @param message   string message to be send to discord log channel characters.
     * @param level     log level
     * @param extension extension.
     * @throws IllegalArgumentException message length is more then 2048 characters.
     * @deprecated Use {@link Extension#getLogger()}
     */
    @Deprecated
    void eDiscordLog(@NotNull String message, @NotNull LogLevel level, @NotNull ExtensionData extension);

    /**
     * Retrieves {@link EmbedBuilder} from config section.
     *
     * @param config the config section.
     * @return the embed builder. returns null if embed is empty or embed is disabled.
     */
    @Nullable EmbedBuilder embedFromSection(@NotNull ConfigurationSection config);

    /**
     * Retrieves {@link EmbedBuilder} from config section. Sets player placeholders automatically.
     *
     * @param config the config
     * @param player the player
     * @return the embed builder. returns null if embed is empty or embed is disabled.
     */
    @Nullable EmbedBuilder embedFromSection(@NotNull ConfigurationSection config, @Nullable OfflinePlayer player);

    /**
     * Sets player placeholders on the message. if player is null same message is returned.
     *
     * @param player  the player
     * @param message the message
     * @return the placeholders. null if the message is null.
     */
    @Nullable String setPlaceholders(@Nullable OfflinePlayer player, @Nullable String message);

    /**
     * Empty string to null string.
     *
     * @param value the value
     * @return the string
     */
    @Nullable String emptyStringToNull(@Nullable String value);

    /**
     * Retrieves log info from section log info data.
     *
     * @param config the config
     * @return the log info data
     */
    @NotNull LogInfoData logInfoFromSection(@NotNull ConfigurationSection config);

    /**
     * Execute commands.
     *
     * @param commands the commands
     */
    void executeCommands(@NotNull List<String> commands);

    /**
     * Execute commands with player placeholders.
     *
     * @param commands the commands
     * @param player   the player
     */
    void executeCommands(@NotNull List<String> commands, @Nullable OfflinePlayer player);

    /**
     * Send webhook message.
     *
     * @param message      the message
     * @param embedBuilder the embed builder
     * @param infoData     the info data
     * @param client       the client
     * @param response     the response
     */
    void sendMessage(@Nullable String message, @Nullable EmbedBuilder embedBuilder, @NotNull LogInfoData infoData, @NotNull JDAWebhookClient client, @Nullable Consumer<ReadonlyMessage> response);


    /**
     * Send bot message.
     *
     * @param message      the message
     * @param embedBuilder the embed builder
     * @param infoData     the info data
     * @param response     the response
     */
    void sendMessage(@Nullable String message, @Nullable EmbedBuilder embedBuilder, @NotNull LogInfoData infoData, @Nullable Consumer<Message> response);

    /**
     * Initiate base logging.
     *
     * @param config the config
     * @param client the client
     * @param player the player
     */
    void initiateBaseLogging(@NotNull ConfigurationSection config, @Nullable JDAWebhookClient client, @Nullable OfflinePlayer player);
}
