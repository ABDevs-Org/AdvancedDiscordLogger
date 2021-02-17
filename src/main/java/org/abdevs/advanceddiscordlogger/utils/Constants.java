package org.abdevs.advanceddiscordlogger.utils;

import me.mattstudios.mf.base.CommandBase;
import me.mattstudios.mf.base.CommandManager;
import net.dv8tion.jda.api.JDA;
import org.abdevs.advanceddiscordlogger.enities.Extension;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Constants {
    public static boolean isJDAEnabled;
    public static String logChannelId;
    public static String logGuildId;
    public static boolean isLogChannel;
    public static List<Extension> enabledExtensions = new ArrayList<>();
    public static JDA jda;
    public static ConcurrentHashMap<Extension, List<Listener>> extensionListeners = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Extension, List<CommandBase>> extensionCommands = new ConcurrentHashMap<>();
    public static CommandManager commandManager;
}
