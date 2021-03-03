package org.abdevs.advanceddiscordlogger.utils;

import kong.unirest.Unirest;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.base.CommandBase;
import org.abdevs.advanceddiscordlogger.AdvancedDiscordLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Utils {
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(colorize("&f&l[&e&lADL&f&l] &f" + message));
    }

    private static Object getPrivateField(Object object, String field) throws SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        final Class<?> clazz = object.getClass();
        final Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        final Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    private static void unRegisterBukkitCommand(String command, List<String> aliases) {
        try {
            final AdvancedDiscordLogger plugin = AdvancedDiscordLogger.getPlugin();
            final Object result = getPrivateField(plugin.getServer().getPluginManager(), "commandMap");
            final SimpleCommandMap commandMap = (SimpleCommandMap) result;
            final Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            final Object map = knownCommandsField.get(commandMap);
            knownCommandsField.setAccessible(false);
            @SuppressWarnings("unchecked") final HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(command);
            for (String alias : aliases)
                if (knownCommands.containsKey(alias) &&
                        knownCommands.get(alias).toString().contains(plugin.getName()))
                    knownCommands.remove(alias);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unRegisterCommand(@NotNull CommandBase command) {

        final Class<?> commandClass = command.getClass();
        final String commandName = commandClass.getAnnotation(me.mattstudios.mf.annotations.Command.class).value();
        final List<String> aliases;
        try {
            final Method getAliasesMethod = CommandBase.class.getDeclaredMethod("getAliases");
            getAliasesMethod.setAccessible(true);
            //noinspection unchecked
            aliases = (List<String>) getAliasesMethod.invoke(command);
            getAliasesMethod.setAccessible(false);
            if (commandClass.isAnnotationPresent(Alias.class))
                aliases.addAll(Arrays.asList(commandClass.getAnnotation(Alias.class).value()));
            unRegisterBukkitCommand(commandName, aliases);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void requestPasteService(String paste, Consumer<String> url) {
        final HashMap<String, Object> params = new HashMap<>();
        params.put("code", paste);
        params.put("title", "Advanced Discord Logger");
        params.put("syntax", "Markup");
        Unirest.post("https://api.teknik.io/v1/Paste").queryString(params).asJsonAsync(response -> {
            if (!response.isSuccess()) url.accept(null);
            else url.accept(response.getBody().getObject().getJSONObject("result").getString("url"));
        });
    }
}
