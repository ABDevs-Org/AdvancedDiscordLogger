package org.abdevs.advanceddiscordlogger.api.managers;

import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface ExtensionManager {
    void loadExtensionsAsync(@NotNull("Extensions folder can't be null") File extensionsFolder);

    void loadExtensions(@NotNull("Extensions folder can't be null") File extensionsFolder);

    void loadExtensionAsync(@NotNull("Extension file name can't be null") String fileName);

    void loadExtension(@NotNull("Extension file name can't be null") String fileName);

    void loadExtensionAsync(@NotNull("Extension File can't be null") File jarFile);

    void loadExtension(@NotNull("Extension File can't be null") File jarFile);

    void unloadAllExtensionsAsync();

    void unloadAllExtensions();

    void unloadExtensionAsync(@NotNull("Extension File can't be null") File jarFile);

    void unloadExtension(@NotNull("Extension File can't be null") File jarFile);

    void unloadExtensionAsync(@NotNull("Extension name can't be null") String extensionName);

    void unloadExtension(@NotNull("Extension name can't be null") String extensionName);

    void unloadExtensionAsync(@NotNull("Extension can't be null") ExtensionData extensionData);

    void unloadExtension(@NotNull ExtensionData extensionData);
}
