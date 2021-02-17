package org.abdevs.advanceddiscordlogger.api;


import org.bukkit.plugin.Plugin;

public interface IExtension {
    default void onEnable(Plugin plugin) {

    }

    default void onDisable(Plugin plugin) {

    }
}
