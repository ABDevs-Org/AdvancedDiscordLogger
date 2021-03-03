package org.abdevs.advanceddiscordlogger.api.base;


import org.abdevs.advanceddiscordlogger.AdvancedDiscordLogger;
import org.abdevs.advanceddiscordlogger.api.managers.Api;
import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.abdevs.advanceddiscordlogger.enities.Logger;
import org.bukkit.plugin.Plugin;

public abstract class Extension implements BaseExtension {

    private ExtensionData extensionData = null;
    private Logger logger = null;

    public ExtensionData getExtensionData() {
        return extensionData;
    }

    public Api getApi() {
        return AdvancedDiscordLogger.getApi();
    }

    public Plugin getPlugin() {
        return AdvancedDiscordLogger.getPlugin();
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
    }

    final void init(ExtensionData extensionData, Logger logger) {
        this.extensionData = extensionData;
        this.logger = logger;
    }
}
