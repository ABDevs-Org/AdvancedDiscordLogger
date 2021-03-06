package org.abdevs.advanceddiscordlogger.api.base;


import me.mattstudios.mf.base.CommandBase;
import org.abdevs.advanceddiscordlogger.AdvancedDiscordLogger;
import org.abdevs.advanceddiscordlogger.api.managers.Api;
import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.abdevs.advanceddiscordlogger.enities.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public abstract class Extension implements BaseExtension {

    private ExtensionData extensionData;
    private Logger logger;
    private ClassLoader classLoader;
    private FileConfiguration config;

    /**
     * @return instance of {@link ExtensionData}.
     */
    @NotNull
    public ExtensionData getExtensionData() {
        return extensionData;
    }

    /**
     * @return instance of {@link Api}.
     */
    @NotNull
    public Api getApi() {
        return AdvancedDiscordLogger.getApi();
    }

    /**
     * @return instance of {@link Plugin}.
     */
    @NotNull
    public Plugin getPlugin() {
        return AdvancedDiscordLogger.getPlugin();
    }

    /**
     * @return instance of {@link Logger}.
     */
    @NotNull
    public Logger getLogger() {
        return logger;
    }

    /**
     * @param listeners {@link Listener} to register.
     */
    public void registerListeners(Listener... listeners) {
        getApi().getExtensionUtils().registerListeners(extensionData, listeners);
    }

    /**
     * @param commands {@link CommandBase} to register.
     */
    public void registerCommands(CommandBase... commands) {
        getApi().getExtensionUtils().registerCommands(extensionData, commands);
    }

    /**
     * @return creates the default config and return the instance.
     * null is returned if failed.
     */
    @Nullable
    public FileConfiguration createDefaultConfig() {
        config = getApi().getExtensionUtils().createOrFetchConfig(extensionData.getName(), classLoader.getClass(), "config.yml");
        return config;
    }

    /**
     * @return default config and return the instance.
     * null is returned if default config is null.
     */
    @Nullable
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * @return reloads the default config and return the instance.
     * null is returned if the default config is null.
     */
    @Nullable
    public FileConfiguration reloadConfig() {
        if (config == null) return null;
        try {
            config.loadFromString(config.saveToString());
            return config;
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fires when an extension is being enabled.
     */
    @Override
    public void onEnable() {

    }

    /**
     * Fires when an extension is being disabled.
     */
    @Override
    public void onDisable() {
    }

    /**
     * called using reflections api in {@link org.abdevs.advanceddiscordlogger.api.managers.ExtensionManager#loadExtension(File)}
     */
    final void init(ExtensionData extensionData, Logger logger, ClassLoader classLoader) {
        this.extensionData = extensionData;
        this.logger = logger;
        this.classLoader = classLoader;
    }
}
