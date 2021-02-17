package org.abdevs.advanceddiscordlogger.commands;

import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.abdevs.advanceddiscordlogger.AdvancedDiscordLogger;
import org.abdevs.advanceddiscordlogger.api.AdvancedDiscordLoggerAPI;
import org.abdevs.advanceddiscordlogger.enities.Extension;
import org.abdevs.advanceddiscordlogger.managers.ExtensionManager;
import org.abdevs.advanceddiscordlogger.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;

@SuppressWarnings("unused")
@Command("adl")
public class Commands extends CommandBase {

    private final AdvancedDiscordLogger plugin;

    public Commands(AdvancedDiscordLogger plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onCommand(CommandSender commandSender) {
        commandSender.sendMessage(Utils.colorize("&bServer running &e&lAdvancedDiscordLogger &bby &aABDevs"));
    }

    @Permission("adl.admin.extensions")
    @SubCommand("extensions")
    public void extensionsCommand(CommandSender commandSender) {
        final TextComponent textComponent = new TextComponent(Utils.colorize("&f&l==========[&a&l&nInstalled Extensions&f&l]==========\n"));
        final List<Extension> enabledExtensions = AdvancedDiscordLoggerAPI.getEnabledExtensions();
        for (int i = 0; i < enabledExtensions.size(); i++) {
            final int count = i + 1;
            textComponent.addExtra(Utils.colorize("&c" + count + ". "));
            final Extension extension = enabledExtensions.get(i);
            final TextComponent extInfo = new TextComponent(Utils.colorize("&a" + extension.getName() + "\n"));
            String extInfoBuilder = "&f&l==========[&6&l&nExtension Info&f&l]==========\n" +
                    "&6Description: &a" + extension.getDescription() + "\n" +
                    "&6Author: &a" + extension.getAuthor() + "\n" +
                    "&6Version: &a" + extension.getVersion();
            extInfo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.colorize(extInfoBuilder))));
            textComponent.addExtra(extInfo);
        }
        commandSender.sendMessage(textComponent);
    }

    @Permission("adl.admin.extension")
    @SubCommand("extension")
    public void extensionCommand(CommandSender sender, @Completion("#extensions") String extensionName, @Completion("#actions") String action) {
        final Extension extension = AdvancedDiscordLoggerAPI.getExtension(extensionName);
        switch (action) {
            case "onEnable()": {
                if (extension == null) {
                    sender.sendMessage(Utils.colorize("&cExtension not found!"));
                    return;
                }
                sender.sendMessage(Utils.colorize("&6Attempting to execute #onEnable() method on extension " + extension.getName()));
                extension.getInstance().onEnable(plugin);
                sender.sendMessage(Utils.colorize("&aExecution finished."));
                break;
            }
            case "onDisable()": {
                if (extension == null) {
                    sender.sendMessage(Utils.colorize("&cExtension not found!"));
                    return;
                }
                sender.sendMessage(Utils.colorize("&6Attempting to execute #onDisable() method on extension " + extension.getName()));
                extension.getInstance().onDisable(plugin);
                sender.sendMessage(Utils.colorize("&aExecution finished."));
                break;
            }
            case "load": {
                sender.sendMessage(Utils.colorize("&aAttempting to load " + extensionName));
                ExtensionManager.loadExtension(extensionName);
                break;
            }
            case "unload": {
                if (extension == null) {
                    sender.sendMessage(Utils.colorize("&cExtension not found!"));
                    return;
                }
                sender.sendMessage(Utils.colorize("&aAttempting to unload " + extensionName));
                ExtensionManager.unLoadExtension(extension);
                break;
            }
        }
    }
}
