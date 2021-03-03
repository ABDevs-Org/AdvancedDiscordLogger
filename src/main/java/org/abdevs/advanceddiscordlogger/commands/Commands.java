package org.abdevs.advanceddiscordlogger.commands;

import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.abdevs.advanceddiscordlogger.AdvancedDiscordLogger;
import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.abdevs.advanceddiscordlogger.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("unused")
@Command("adl")
public class Commands extends CommandBase {

    @Default
    public void onCommand(CommandSender commandSender) {
        commandSender.sendMessage(Utils.colorize("&bServer running &e&lAdvancedDiscordLogger &bby &aABDevs"));
    }

    @Permission("adl.admin.extensions")
    @SubCommand("extensions")
    public void extensionsCommand(CommandSender commandSender) {
        final TextComponent textComponent = new TextComponent(Utils.colorize("&f&l==========[&a&l&nInstalled Extensions&f&l]==========\n"));
        final List<ExtensionData> enabledExtensionData = AdvancedDiscordLogger.getApi().getEnabledExtensions();
        for (int i = 0; i < enabledExtensionData.size(); i++) {
            final int count = i + 1;
            textComponent.addExtra(Utils.colorize("&c" + count + ". "));
            final ExtensionData extensionData = enabledExtensionData.get(i);
            final TextComponent extInfo = new TextComponent(Utils.colorize("&a" + extensionData.getName() + "\n"));
            String extInfoBuilder = "&f&l==========[&6&l&nExtension Info&f&l]==========\n" +
                    "&6Description: &a" + extensionData.getDescription() + "\n" +
                    "&6Author: &a" + extensionData.getAuthor() + "\n" +
                    "&6Version: &a" + extensionData.getVersion();
            extInfo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.colorize(extInfoBuilder))));
            textComponent.addExtra(extInfo);
        }
        commandSender.sendMessage(textComponent);
    }

    @Permission("adl.admin.extension")
    @SubCommand("extension")
    public void extensionCommand(CommandSender sender, @Completion("#extensions") String extensionName, @Completion("#actions") String action) {
        final ExtensionData extensionData = AdvancedDiscordLogger.getApi().getExtension(extensionName);
        switch (action) {
            case "onEnable()": {
                if (extensionData == null) {
                    sender.sendMessage(Utils.colorize("&cExtension not found!"));
                    return;
                }
                sender.sendMessage(Utils.colorize("&6Attempting to execute #onEnable() method on extension " + extensionData.getName()));
                extensionData.getInstance().onEnable();
                sender.sendMessage(Utils.colorize("&aExecution finished."));
                break;
            }
            case "onDisable()": {
                if (extensionData == null) {
                    sender.sendMessage(Utils.colorize("&cExtension not found!"));
                    return;
                }
                sender.sendMessage(Utils.colorize("&6Attempting to execute #onDisable() method on extension " + extensionData.getName()));
                extensionData.getInstance().onDisable();
                sender.sendMessage(Utils.colorize("&aExecution finished."));
                break;
            }
            case "load": {
                sender.sendMessage(Utils.colorize("&aAttempting to load " + extensionName));
                AdvancedDiscordLogger.getPlugin().getExtensionManager().loadExtension(extensionName);
                break;
            }
            case "unload": {
                if (extensionData == null) {
                    sender.sendMessage(Utils.colorize("&cExtension not found!"));
                    return;
                }
                sender.sendMessage(Utils.colorize("&aAttempting to unload " + extensionName));
                AdvancedDiscordLogger.getPlugin().getExtensionManager().unloadExtension(extensionData);
                break;
            }
        }
    }


    @Permission("adl.admin.dump")
    @SubCommand("dump")
    public void dumpCommand(CommandSender sender) {
        final JDA jda = AdvancedDiscordLogger.getPlugin().getJda();
        final boolean isJdaReady = AdvancedDiscordLogger.getApi().isJDAReady();
        final CompletableFuture<Long> pingFuture;
        if (isJdaReady)
            //noinspection ConstantConditions
            pingFuture = jda.getRestPing().submit();
        else pingFuture = null;
        final AdvancedDiscordLogger plugin = AdvancedDiscordLogger.getPlugin();
        final List<ExtensionData> enabledExtensionData = plugin.getEnabledExtensionData();
        final Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins();
        //noinspection SpellCheckingInspection
        final String config = plugin.getConfig().saveToString().replaceAll("[MN][A-Za-z\\d]{23}\\.[\\w-]{6}\\.[\\w-]{27}", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        final StringBuilder dump = new StringBuilder();
        dump.append("======Plugin Info======\n" + "\n" + "Name: ").append(plugin.getDescription().getName()).append("\n").append("Version: ").append(plugin.getDescription().getVersion()).append("\n").append("\n");
        dump.append("======Enabled Extensions======\n").append("\n");
        enabledExtensionData.forEach(extension -> dump.append("=> ").append(extension.getName()).append(":\n").append("   Version: ").append(extension.getVersion()).append("\n")
                .append("   Author: ").append(extension.getAuthor()).append("\n"));
        dump.append("\n");
        dump.append("======Loaded Plugins======\n").append("\n");
        for (Plugin loadedPlugin : plugins)
            dump.append("=> ").append(loadedPlugin.getDescription().getFullName()).append("\n");
        dump.append("\n");
        dump.append("======JDA Info======\n").append("\n").append("isReady = ").append(isJdaReady).append("\n");
        if (isJdaReady) {
            final int guildCount = jda.getGuilds().size();
            final int channelCount = jda.getTextChannels().size() + jda.getVoiceChannels().size();
            int memberCount = 0;
            for (Guild guild : jda.getGuilds()) memberCount = memberCount + guild.getMemberCount();
            final long gatewayPing = jda.getGatewayPing();
            dump.append("GuildCount = ").append(guildCount).append("\n").append("Channel Count = ").append(channelCount).append("\n")
                    .append("Member Count = ").append(memberCount).append("\n");
            long ping = -1;
            try {
                ping = pingFuture.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
            }
            dump.append("Ping = ").append(ping == -1 ? "Timed Out" : ping).append("ms\n").append("WebSocket Ping = ").append(gatewayPing).append("ms\n").append("\n");
        } else dump.append("\n");
        dump.append("======Config File======\n").append("\n").append("\n").append(config);

        Utils.requestPasteService(dump.toString(), url -> {
            if (url == null) {
                final File file = new File(plugin.getDataFolder(), "adl-dump.txt");
                try (final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                    writer.write(dump.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(Utils.colorize("Paste service is unavailable at the moment. Create a dump file in " + file.getAbsolutePath()));
                return;
            }
            sender.sendMessage(Utils.colorize("&aDump has been pasted on &e" + url));
        });
    }
}
