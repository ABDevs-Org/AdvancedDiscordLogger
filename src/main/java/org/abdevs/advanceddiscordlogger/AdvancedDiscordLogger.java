package org.abdevs.advanceddiscordlogger;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.abdevs.advanceddiscordlogger.api.managers.*;
import org.abdevs.advanceddiscordlogger.api.utils.ExtensionUtils;
import org.abdevs.advanceddiscordlogger.api.utils.ExtensionUtilsImpl;
import org.abdevs.advanceddiscordlogger.commands.Commands;
import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.abdevs.advanceddiscordlogger.listeners.PingCommand;
import org.abdevs.advanceddiscordlogger.utils.Utils;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public final class AdvancedDiscordLogger extends JavaPlugin {

    private static final Api api = new ApiImpl();
    private final ExtensionUtils extensionUtils = new ExtensionUtilsImpl();
    private final ExtensionManager extensionManager = new ExtensionManagerImpl();
    private final WebhookManager webhookManager = new WebhookManagerImpl();
    private final List<ExtensionData> enabledExtensionData = new ArrayList<>();
    private final ConcurrentHashMap<ExtensionData, List<Listener>> extensionListeners = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ExtensionData, List<CommandBase>> extensionCommands = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ExtensionData, List<JDAWebhookClient>> extensionWebhooks = new ConcurrentHashMap<>();
    private CommandManager commandManager;
    private String logChannelId;
    private String logGuildId;
    private boolean isLogChannel;
    private JDA jda;

    /**
     * @return API to be used to communicate with the plugin.
     */
    @NotNull
    public static Api getApi() {
        return api;
    }

    /**
     * @return instance of this plugin class.
     */
    @NotNull
    public static AdvancedDiscordLogger getPlugin() {
        return getPlugin(AdvancedDiscordLogger.class);
    }

    public WebhookManager getWebhookManager() {
        return webhookManager;
    }

    @NotNull
    public ConcurrentHashMap<ExtensionData, List<JDAWebhookClient>> getExtensionWebhooks() {
        return extensionWebhooks;
    }

    @NotNull
    public ExtensionUtils getExtensionUtils() {
        return extensionUtils;
    }

    @NotNull
    public ExtensionManager getExtensionManager() {
        return extensionManager;
    }

    @Nullable
    public String getLogChannelId() {
        return logChannelId;
    }

    @Nullable
    public String getLogGuildId() {
        return logGuildId;
    }

    public boolean isLogChannel() {
        return isLogChannel;
    }

    @NotNull
    public List<ExtensionData> getEnabledExtensionData() {
        return enabledExtensionData;
    }

    @NotNull
    public ConcurrentHashMap<ExtensionData, List<Listener>> getExtensionListeners() {
        return extensionListeners;
    }

    @NotNull
    public ConcurrentHashMap<ExtensionData, List<CommandBase>> getExtensionCommands() {
        return extensionCommands;
    }

    @NotNull
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Nullable
    public JDA getJda() {
        return jda;
    }

    private void registerCommands() {
        commandManager = new CommandManager(this);
        commandManager.getCompletionHandler().register("#options", input -> Arrays.asList("status", "disconnect", "connect", "reconnect"));
        commandManager.getCompletionHandler().register("#actions", input -> Arrays.asList("onEnable()", "onDisable()", "load", "unload"));
        commandManager.getCompletionHandler().register("#extensions", input ->
                enabledExtensionData.stream().map(ExtensionData::getName).collect(Collectors.toList()));
        commandManager.register(new Commands());
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        final FileConfiguration config = getConfig();
        final boolean isJDAEnabled = config.getBoolean("api.JDA.enabled");
        isLogChannel = config.getBoolean("api.JDA.log-channel");
        logChannelId = config.getString("api.JDA.log-channel-id");
        logGuildId = config.getString("api.JDA.log-guild-id");

        if (isJDAEnabled) {
            jda = initJDA(config);
            if (jda != null) printInformation();
        }

        Utils.log("&aActive APIs to be used by other plugins:");
        if (isJDAEnabled)
            Utils.log(" &c- &bJDA (Java Discord API) By DV8FromTheWorld");
        Utils.log(" &c- &bDiscord-Webhooks By MinnDevelopment, Modified By ABDevs");
        Utils.log("");
        Utils.log("&b            ____   &d_____                    ");
        Utils.log("&b     /\\    |  _ \\ &d|  __ \\                   ");
        Utils.log("&b    /  \\   | |_) |&d| |  | |  ___ __   __ ___ ");
        Utils.log("&b   / /\\ \\  |  _ < &d| |  | | / _ \\\\ \\ / // __|");
        Utils.log("&b  / ____ \\ | |_) |&d| |__| ||  __/ \\ V / \\__ \\");
        Utils.log("&b /_/    \\_\\|____/ &d|_____/  \\___|  \\_/  |___/");
        Utils.log("");
        Utils.log("&c&lLoading extensions...");
        registerCommands();
        extensionManager.loadExtensions(new File(getDataFolder(), "extensions"));
        new Metrics(this, 8235);
        Utils.log("&aSuccessfully enabled &e&lAdvancedDiscordLogger");
    }

    @Override
    public void onDisable() {
        extensionManager.unloadAllExtensions();
        Utils.log("&cSuccessfully disabled &e&lAdvancedDiscordLogger");
    }

    private JDA initJDA(FileConfiguration config) {
        final String token = config.getString("api.JDA.token");
        if (token == null || token.equalsIgnoreCase("")) {
            Utils.log("&c&lNo login token provided! The API will not work as expected!");
            return null;
        }
        final List<String> enabledGatewayIntents = config.getStringList("api.JDA.enabled-gateway-intents");
        final List<String> enabledCacheFlagsString = config.getStringList("api.JDA.enabled-cache-flags");
        final List<String> disabledCacheFlagsString = config.getStringList("api.JDA.disabled-cache-flags");
        final List<GatewayIntent> gatewayIntents = new ArrayList<>();
        final List<CacheFlag> enabledCacheFlags = new ArrayList<>();
        final List<CacheFlag> disabledCacheFlags = new ArrayList<>();
        enabledGatewayIntents.forEach(sIntent -> gatewayIntents.add(GatewayIntent.valueOf(sIntent)));
        enabledCacheFlagsString.forEach(sFlag -> enabledCacheFlags.add(CacheFlag.valueOf(sFlag)));
        disabledCacheFlagsString.forEach(sFlag -> disabledCacheFlags.add(CacheFlag.valueOf(sFlag)));
        try {
            final JDABuilder builder = JDABuilder.createDefault(token, gatewayIntents)
                    .disableCache(disabledCacheFlags).enableCache(enabledCacheFlags);
            if (config.getBoolean("api.JDA.cache-all-member")) builder.setMemberCachePolicy(MemberCachePolicy.ALL);
            if (config.getBoolean("api.JDA.ping-command.enable"))
                builder.addEventListeners(new PingCommand(config));
            return builder.build().awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
            Utils.log("&c&lFailed to start the discord bot...API will not work as expected!");
            return null;
        }
    }

    private void printInformation() {
        final SelfUser selfUser = jda.getSelfUser();
        final String botName = selfUser.getAsTag();
        final List<Guild> guilds = jda.getGuilds();
        final EnumSet<GatewayIntent> gatewayIntents = jda.getGatewayIntents();
        final List<String> intentsNames = new ArrayList<>();
        gatewayIntents.forEach(gatewayIntent -> intentsNames.add(gatewayIntent.name()));
        final String stringIntents = String.join(", ", intentsNames);
        Utils.log("&aSuccessfully logged in with &e" + botName + "&a account!");
        Utils.log("&aEnabled Intents: &r" + stringIntents);
        Utils.log("&f-------------------------[&bGuilds&f]-------------------------");
        guilds.forEach(guild -> {
            Utils.log("&b&l" + guild.getName() + ":");
            final List<TextChannel> textChannels = guild.getTextChannels();
            final List<VoiceChannel> voiceChannels = guild.getVoiceChannels();
            Utils.log("  &cText Channels:");
            textChannels.forEach(textChannel -> Utils.log(" - " + textChannel.getName() + " : " + textChannel.getId()));
            Utils.log("  &cVoice Channels:");
            voiceChannels.forEach(voiceChannel -> Utils.log(" - " + voiceChannel.getName() + " : " + voiceChannel.getId()));
        });
    }
}
