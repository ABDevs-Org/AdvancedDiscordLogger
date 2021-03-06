package org.abdevs.advanceddiscordlogger.enities;

import org.abdevs.advanceddiscordlogger.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class Logger {
    private final ExtensionData extensionData;

    public Logger(ExtensionData extensionData) {
        this.extensionData = extensionData;
    }

    public Logger info(@NotNull String message) {
        Utils.log("&c[&b" + extensionData.getName() + "&c] &r" + message);
        return this;
    }

    public Logger warn(@NotNull String message) {
        Utils.log("&c[&b" + extensionData.getName() + "&c] &6" + message);
        return this;
    }

    public Logger error(@NotNull String message) {
        Utils.log("&c[&b" + extensionData.getName() + "&c] &c" + message);
        return this;
    }

    public void discordInfo(@NotNull String message) {
        Utils.sendDiscordLog(message, LogLevel.INFO, extensionData);
    }

    public void discordWarn(@NotNull String message) {
        Utils.sendDiscordLog(message, LogLevel.WARN, extensionData);
    }

    public void discordError(@NotNull String message) {
        Utils.sendDiscordLog(message, LogLevel.SEVERE, extensionData);
    }

    public void discordSuccess(@NotNull String message) {
        Utils.sendDiscordLog(message, LogLevel.SUCCESS, extensionData);
    }
}
