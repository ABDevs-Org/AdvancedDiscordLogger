package org.abdevs.advanceddiscordlogger.api.base;

public interface BaseExtension {
    /**
     * Fires when an extension is being enabled.
     */
    void onEnable();

    /**
     * Fires when an extension is being disabled.
     */
    void onDisable();
}
