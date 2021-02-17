package org.abdevs.advanceddiscordlogger;

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
import org.abdevs.advanceddiscordlogger.api.AdvancedDiscordLoggerAPI;
import org.abdevs.advanceddiscordlogger.commands.Commands;
import org.abdevs.advanceddiscordlogger.enities.Extension;
import org.abdevs.advanceddiscordlogger.managers.ExtensionManager;
import org.abdevs.advanceddiscordlogger.utils.Constants;
import org.abdevs.advanceddiscordlogger.utils.Utils;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public final class AdvancedDiscordLogger extends JavaPlugin {

    private static AdvancedDiscordLogger plugin;

    public static AdvancedDiscordLogger getPlugin() {
        return plugin;
    }

    private void registerCommands() {
        Constants.commandManager = new CommandManager(this);
        final CommandManager commandManager = AdvancedDiscordLoggerAPI.getCommandManager();
        commandManager.getCompletionHandler().register("#options", input -> Arrays.asList("status", "disconnect", "connect", "reconnect"));
        commandManager.getCompletionHandler().register("#actions", input -> Arrays.asList("onEnable()", "onDisable()", "load", "unload"));
        commandManager.getCompletionHandler().register("#extensions", input ->
                AdvancedDiscordLoggerAPI.getEnabledExtensions().stream().map(Extension::getName).collect(Collectors.toList()));
        commandManager.register(new Commands(this));
    }

    @Override
    public void onEnable() {
        AdvancedDiscordLogger.plugin = this;

        saveDefaultConfig();
        final FileConfiguration config = getConfig();

        Constants.isJDAEnabled = config.getBoolean("api.JDA.enabled");
        Constants.isLogChannel = config.getBoolean("api.JDA.log-channel");
        Constants.logChannelId = config.getString("api.JDA.log-channel-id");
        Constants.logGuildId = config.getString("api.JDA.log-guild-id");

        if (Constants.isJDAEnabled) {
            final String token = config.getString("api.JDA.token");
            if (token == null || token.equalsIgnoreCase("")) {
                Utils.log("&c&lNo login token provided! The API will not work as expected!");
                return;
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
                Constants.jda = builder.build().awaitReady();
            } catch (LoginException | InterruptedException e) {
                e.printStackTrace();
                Utils.log("&c&lFailed to start the discord bot...API will not work as expected!");
                return;
            }
        }

        printInformation();
        Utils.log("&aActive APIs to be used by other plugins:");
        if (Constants.isJDAEnabled)
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
        ExtensionManager.loadExtensions(new File(AdvancedDiscordLogger.plugin.getDataFolder(), "extensions"));
        Utils.log("&aSuccessfully enabled &e&lAdvancedDiscordLogger");

        new Metrics(this, 8235);
    }

    @Override
    public void onDisable() {
        ExtensionManager.unLoadAllExtensions();
        Utils.log("&cSuccessfully disabled &e&lAdvancedDiscordLogger");
    }

    @SuppressWarnings("ConstantConditions")
    private void printInformation() {
        if (!AdvancedDiscordLoggerAPI.isJDAReady()) return;
        final JDA jda = AdvancedDiscordLoggerAPI.getJda();
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
