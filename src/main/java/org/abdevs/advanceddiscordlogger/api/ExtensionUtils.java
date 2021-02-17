package org.abdevs.advanceddiscordlogger.api;

import me.mattstudios.mf.base.CommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.md_5.bungee.api.ChatColor;
import org.abdevs.advanceddiscordlogger.AdvancedDiscordLogger;
import org.abdevs.advanceddiscordlogger.enities.Extension;
import org.abdevs.advanceddiscordlogger.enities.LogLevel;
import org.abdevs.advanceddiscordlogger.utils.Constants;
import org.abdevs.advanceddiscordlogger.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import java.util.stream.Collectors;

@SuppressWarnings({"ConstantConditions", "unused"})
public class ExtensionUtils {

    private ExtensionUtils() {
    }

    /**
     * @param folderName Name of the extension folder
     * @param clazz      Main class of the extension
     * @param fileName   Name of the resource file
     * @return configuration
     */
    @Nullable
    public static FileConfiguration createOrFetchConfig(@NotNull String folderName, @NotNull Class<?> clazz, @NotNull String fileName) {
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

    /**
     * @param extensionName Name of the extension.
     * @param listeners     listeners to register.
     * @throws IllegalArgumentException if no extension with name extensionName is active or exists.
     */
    public static void registerListeners(@NotNull String extensionName, @NotNull Listener... listeners) {
        final ConcurrentHashMap<Extension, List<Listener>> extensionListeners = AdvancedDiscordLoggerAPI.getExtensionListeners();
        final List<String> extensionsByName = AdvancedDiscordLoggerAPI.getEnabledExtensions().stream().map(Extension::getName).collect(Collectors.toList());
        if (!extensionsByName.contains(extensionName))
            throw new IllegalArgumentException("Extension named " + extensionName + " is not active or does not exists.");
        final Extension extInfo = AdvancedDiscordLoggerAPI.getExtension(extensionName);
        for (Listener listener : listeners)
            Bukkit.getPluginManager().registerEvents(listener, AdvancedDiscordLogger.getPlugin());
        final List<Listener> listenersToAdd;
        if (extensionListeners.containsKey(extInfo))
            listenersToAdd = extensionListeners.get(extInfo);
        else listenersToAdd = new ArrayList<>();
        listenersToAdd.addAll(Arrays.asList(listeners));
        Constants.extensionListeners.put(extInfo, listenersToAdd);
    }

    /**
     * @param extensionName Name of the extension.
     * @param commands      commands to register.
     * @throws IllegalArgumentException if no extension with name extensionName is active or exists.
     */
    public static void registerCommands(@NotNull String extensionName, @NotNull CommandBase... commands) {
        final ConcurrentHashMap<Extension, List<CommandBase>> extensionCommands = AdvancedDiscordLoggerAPI.getExtensionCommands();
        final List<String> extensionsByName = AdvancedDiscordLoggerAPI.getEnabledExtensions().stream().map(Extension::getName).collect(Collectors.toList());
        if (!extensionsByName.contains(extensionName))
            throw new IllegalArgumentException("Extension named " + extensionName + " is not active or does not exists.");
        final Extension extInfo = AdvancedDiscordLoggerAPI.getExtension(extensionName);
        AdvancedDiscordLoggerAPI.getCommandManager().register(commands);
        final List<CommandBase> commandsToAdd;
        if (extensionCommands.containsKey(extInfo))
            commandsToAdd = extensionCommands.get(extInfo);
        else commandsToAdd = new ArrayList<>();
        commandsToAdd.addAll(Arrays.asList(commands));
        Constants.extensionCommands.put(extInfo, commandsToAdd);
    }

    /**
     * @param message string message to be parsed
     * @return message with color codes
     */
    @NotNull
    public static String colorize(@NotNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * @param message       string message to print on the console
     * @param extensionName name of the extension
     * @throws IllegalArgumentException if no extension with name extensionName is active or exists.
     */
    public static void eLog(@NotNull String message, @NotNull String extensionName) {
        final List<String> extensionsByName = AdvancedDiscordLoggerAPI.getEnabledExtensions().stream().map(Extension::getName).collect(Collectors.toList());
        if (!extensionsByName.contains(extensionName))
            throw new IllegalArgumentException("Extension named " + extensionName + " is not active or does not exists.");
        Utils.log("&c[&b" + extensionName + "&c] &r" + message);
    }

    /**
     * Sends log message to discord. Length of message can't be more than 2048
     *
     * @param message       string message to be send to discord log channel characters.
     * @param level         log level
     * @param extensionName name of the extension.
     * @throws IllegalArgumentException if no extension with name extensionName is active or exists or
     *                                  {@param message} length is more then 2048 characters.
     */
    public static void eDiscordLog(@NotNull String message, @NotNull LogLevel level, @NotNull String extensionName) {
        if (!Constants.isLogChannel) return;
        if (message.length() > 2048)
            throw new IllegalArgumentException("Length of message can't be more than 2048 characters");
        final List<String> extensionsByName = AdvancedDiscordLoggerAPI.getEnabledExtensions().stream().map(Extension::getName).collect(Collectors.toList());
        if (!extensionsByName.contains(extensionName))
            throw new IllegalArgumentException("Extension named " + extensionName + " is not active or does not exists.");
        final JDA jda = AdvancedDiscordLoggerAPI.getJda();
        final Guild guild = jda.getGuildById(Constants.logGuildId);
        if (guild == null) {
            Utils.log("&cThe log guild id provided is invalid!");
            return;
        }
        final TextChannel channel = guild.getTextChannelById(Constants.logChannelId);
        if (channel == null) {
            Utils.log("&cThe log channel id provided is invalid!");
            return;
        }
        final EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(message).setAuthor(extensionName)
                .setTimestamp(Instant.now()).setFooter("CentralizedDiscordSystem");
        switch (level) {
            case SUCCESS: {
                builder.setColor(Color.GREEN);
                break;
            }
            case INFO: {
                builder.setColor(Color.CYAN);
                break;
            }
            case WARN: {
                builder.setColor(0xfa8e3c);
                break;
            }
            case SEVERE: {
                builder.setColor(Color.RED);
                break;
            }
        }
        channel.sendMessage(builder.build()).queue();
    }
}
