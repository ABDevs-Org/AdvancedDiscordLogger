package org.abdevs.advanceddiscordlogger.api.utils;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import me.mattstudios.mf.base.CommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.md_5.bungee.api.ChatColor;
import org.abdevs.advanceddiscordlogger.AdvancedDiscordLogger;
import org.abdevs.advanceddiscordlogger.api.managers.Api;
import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.abdevs.advanceddiscordlogger.enities.LogInfoData;
import org.abdevs.advanceddiscordlogger.enities.LogLevel;
import org.abdevs.advanceddiscordlogger.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings({"unused"})
public class ExtensionUtilsImpl implements ExtensionUtils {

    @Override
    @Nullable
    public FileConfiguration createOrFetchConfig(@NotNull String folderName, @NotNull Class<?> clazz, @NotNull String fileName) {
        final File folder = new File(AdvancedDiscordLogger.getPlugin().getDataFolder(), "/extensions/" + folderName);
        if (!folder.exists()) //noinspection ResultOfMethodCallIgnored
            folder.mkdir();
        final File file = new File(folder, fileName);
        if (!file.exists()) {
            final Method findResourceMethod;
            try {
                findResourceMethod = clazz.getClassLoader().getClass().getDeclaredMethod("findResource", String.class);
                final URL invoke = (URL) findResourceMethod.invoke(clazz.getClassLoader(), fileName);
                try (InputStream in = invoke.openStream()) {
                    Files.copy(in, file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        try {
            final YamlConfiguration config = new YamlConfiguration();
            config.load(new File(folder, fileName));
            return config;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void registerListeners(@NotNull ExtensionData extension, @NotNull Listener... listeners) {
        final AdvancedDiscordLogger plugin = AdvancedDiscordLogger.getPlugin();
        final ConcurrentHashMap<ExtensionData, List<Listener>> extensionListeners = plugin.getExtensionListeners();
        for (Listener listener : listeners)
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        final List<Listener> listenersToAdd;
        if (extensionListeners.containsKey(extension))
            listenersToAdd = extensionListeners.get(extension);
        else listenersToAdd = new ArrayList<>();
        listenersToAdd.addAll(Arrays.asList(listeners));
        extensionListeners.put(extension, listenersToAdd);
    }

    @Override
    public void registerCommands(@NotNull ExtensionData extension, @NotNull CommandBase... commands) {
        final AdvancedDiscordLogger plugin = AdvancedDiscordLogger.getPlugin();
        final ConcurrentHashMap<ExtensionData, List<CommandBase>> extensionCommands = plugin.getExtensionCommands();
        plugin.getCommandManager().register(commands);
        final List<CommandBase> commandsToAdd;
        if (extensionCommands.containsKey(extension))
            commandsToAdd = extensionCommands.get(extension);
        else commandsToAdd = new ArrayList<>();
        commandsToAdd.addAll(Arrays.asList(commands));
        extensionCommands.put(extension, commandsToAdd);
    }

    @Override
    @NotNull
    public String colorize(@NotNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    @Deprecated
    public void eLog(@NotNull String message, @NotNull ExtensionData extension) {
        Utils.log("&c[&b" + extension.getName() + "&c] &r" + message);
    }

    @Override
    @Deprecated
    public void eDiscordLog(@NotNull String message, @NotNull LogLevel level, @NotNull ExtensionData extension) {
        Utils.sendDiscordLog(message, level, extension);
    }

    @Override
    @Nullable
    public EmbedBuilder embedFromSection(@NotNull ConfigurationSection config) {
        return embedFromSection(config, null);
    }

    @Override
    @Nullable
    public EmbedBuilder embedFromSection(@NotNull ConfigurationSection config, @Nullable OfflinePlayer player) {
        final boolean isEnabled = config.getBoolean("enable");
        if (!isEnabled) return null;
        final String authorName = setPlaceholders(player, emptyStringToNull(config.getString("author.name")));
        final String authorUrl = setPlaceholders(player, emptyStringToNull(config.getString("author.url")));
        final String authorIconUrl = setPlaceholders(player, emptyStringToNull(config.getString("author.iconUrl")));
        final String title = setPlaceholders(player, emptyStringToNull(config.getString("title.text")));
        final String titleUrl = setPlaceholders(player, emptyStringToNull(config.getString("title.url")));
        final List<String> descriptionList = config.getStringList("description");
        final String description = setPlaceholders(player, emptyStringToNull(String.join("\n", descriptionList)));
        final List<String> fieldsList = config.getStringList("fields");
        final int color = config.getInt("color");
        final String footer = setPlaceholders(player, emptyStringToNull(config.getString("footer.text")));
        final String footerIconUrl = setPlaceholders(player, emptyStringToNull(config.getString("footer.iconUrl")));
        final boolean isTimestamp = config.getBoolean("timestamp");
        final String imageUrl = setPlaceholders(player, emptyStringToNull(config.getString("image")));
        final String thumbnailUrl = setPlaceholders(player, emptyStringToNull(config.getString("thumbnail")));
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(authorName, authorUrl, authorIconUrl)
                .setTitle(title, titleUrl)
                .setDescription(description)
                .setColor(color)
                .setFooter(footer, footerIconUrl)
                .setImage(imageUrl)
                .setThumbnail(thumbnailUrl);
        if (isTimestamp) embedBuilder.setTimestamp(Instant.now());
        fieldsList.forEach(s -> {
            if (!s.equals("blank")) {
                final String[] split = s.split(";", 3);
                final String name = setPlaceholders(player, emptyStringToNull(split[0]));
                final String value = setPlaceholders(player, emptyStringToNull(split[1]));
                final String inLine = setPlaceholders(player, emptyStringToNull(split[2]));
                embedBuilder.addField(name, value, Boolean.parseBoolean(inLine));
                return;
            }
            embedBuilder.addBlankField(true);
        });
        return embedBuilder.isEmpty() ? null : embedBuilder;
    }

    @Override
    @Nullable
    public String setPlaceholders(@Nullable OfflinePlayer player, @Nullable String message) {
        if (message == null) return null;
        if (player == null) return message;
        return PlaceholderAPI.setPlaceholders(player, message);
    }

    @Override
    @Nullable
    public String emptyStringToNull(@Nullable String value) {
        if (value == null || value.equals("")) return null;
        return value;
    }

    @Override
    @NotNull
    public LogInfoData logInfoFromSection(@NotNull ConfigurationSection config) {
        final String channelIdS = emptyStringToNull(config.getString("channel-id"));
        final boolean isWebhook = config.getBoolean("use-webhook");
        final String webhookUrl = emptyStringToNull(config.getString("webhook-url"));
        final String username = emptyStringToNull(config.getString("username"));
        final String avatarUrl = emptyStringToNull(config.getString("avatar-url"));
        long channelId = 0;
        if (channelIdS != null) try {
            channelId = Long.parseLong(channelIdS);
        } catch (NumberFormatException ignored) {
        }
        return new LogInfoData(isWebhook, webhookUrl, username, avatarUrl, channelId);
    }

    @Override
    public void executeCommands(@NotNull List<String> commands) {
        executeCommands(commands, null);
    }

    @Override
    public void executeCommands(@NotNull List<String> commands, @Nullable OfflinePlayer player) {
        final AdvancedDiscordLogger plugin = AdvancedDiscordLogger.getPlugin();
        plugin.getServer().getScheduler().runTask(plugin, () ->
                commands.forEach(command -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), PlaceholderAPI.setPlaceholders(player, command))));
    }

    @Override
    public void sendMessage(@Nullable String message, @Nullable EmbedBuilder embedBuilder, @NotNull LogInfoData infoData, @NotNull JDAWebhookClient client, @Nullable Consumer<ReadonlyMessage> response) {
        if (!infoData.isWebhook() || client.isShutdown() || (message == null && embedBuilder == null)) {
            if (response != null) response.accept(null);
            return;
        }
        final WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
        messageBuilder.setContent(message)
                .setAvatarUrl(infoData.getAvatarUrl())
                .setUsername(infoData.getUsername());
        if (embedBuilder != null) messageBuilder.addEmbeds(WebhookEmbedBuilder.fromJDA(embedBuilder.build()).build());
        if (messageBuilder.isEmpty()) {
            if (response != null) response.accept(null);
            return;
        }
        client.send(messageBuilder.build()).thenAccept(readonlyMessage -> {
            if (response != null) response.accept(readonlyMessage);
        });
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void sendMessage(@Nullable String message, @Nullable EmbedBuilder embedBuilder, @NotNull LogInfoData infoData, @Nullable Consumer<Message> response) {
        final @NotNull Api api = AdvancedDiscordLogger.getApi();
        if (infoData.isWebhook() || !api.isJDAReady() || (message == null && embedBuilder == null)) {
            if (response != null) response.accept(null);
            return;
        }
        final long channelId = infoData.getChannelId();
        final JDA jda = AdvancedDiscordLogger.getPlugin().getJda();
        final TextChannel channel = jda.getTextChannelById(channelId);
        if (message != null && embedBuilder != null) {
            channel.sendMessage(message).embed(embedBuilder.build()).queue(reqMessage -> {
                if (response != null) response.accept(reqMessage);
            });
            return;
        }
        if (embedBuilder == null) {
            channel.sendMessage(message).queue(reqMessage -> {
                if (response != null) response.accept(reqMessage);
            });
            return;
        }
        channel.sendMessage(embedBuilder.build()).queue(reqMessage -> {
            if (response != null) response.accept(reqMessage);
        });
    }

    @Override
    public void initiateBaseLogging(@NotNull ConfigurationSection config, @Nullable JDAWebhookClient client, @Nullable OfflinePlayer player) {
        if (!config.getBoolean("enable")) return;
        if (player != null && player.isOnline()) {
            final String bypassPerms = config.getString("bypass-perm");
            if (bypassPerms != null) if (((Player) player).hasPermission(bypassPerms)) return;
        }
        final List<String> actionCommands = config.getStringList("actions.commands");
        executeCommands(actionCommands, player);
        final ConfigurationSection logMessageSection = config.getConfigurationSection("log-message");
        if (logMessageSection == null) return;
        final String message = setPlaceholders(player,emptyStringToNull(logMessageSection.getString("message")));
        final ConfigurationSection embedSection = logMessageSection.getConfigurationSection("embed");
        final EmbedBuilder embedBuilder;
        if (embedSection != null) embedBuilder = embedFromSection(embedSection, player);
        else embedBuilder = null;
        if (message == null && embedBuilder == null) return;
        final ConfigurationSection logInfoSection = config.getConfigurationSection("log-info");
        if (logInfoSection == null) return;
        final LogInfoData logInfoData = logInfoFromSection(logInfoSection);
        if (logInfoData.isWebhook()) {
            if (client == null) return;
            sendMessage(message, embedBuilder, logInfoData, client, null);
            return;
        }
        sendMessage(message, embedBuilder, logInfoData, null);
    }
}
