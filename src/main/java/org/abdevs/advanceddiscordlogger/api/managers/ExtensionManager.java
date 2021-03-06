package org.abdevs.advanceddiscordlogger.api.managers;

import org.abdevs.advanceddiscordlogger.enities.ExtensionData;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface ExtensionManager {

    /**
     * Load extensions in folder async.
     *
     * @param extensionsFolder the extensions folder
     */
    void loadExtensionsAsync(@NotNull("Extensions folder can't be null") File extensionsFolder);

    /**
     * Load extensions in folder.
     *
     * @param extensionsFolder the extensions folder
     */
    void loadExtensions(@NotNull("Extensions folder can't be null") File extensionsFolder);

    /**
     * Load extension async.
     *
     * @param fileName the file name
     */
    void loadExtensionAsync(@NotNull("Extension file name can't be null") String fileName);

    /**
     * Load extension.
     *
     * @param fileName the file name
     */
    void loadExtension(@NotNull("Extension file name can't be null") String fileName);

    /**
     * Load extension async.
     *
     * @param jarFile the jar file
     */
    void loadExtensionAsync(@NotNull("Extension File can't be null") File jarFile);

    /**
     * Load extension.
     *
     * @param jarFile the jar file
     */
    void loadExtension(@NotNull("Extension File can't be null") File jarFile);

    /**
     * Un load all extensions async.
     */
    void unloadAllExtensionsAsync();

    /**
     * Un load all extensions.
     */
    void unloadAllExtensions();

    /**
     * Un load extension async.
     *
     * @param jarFile the jar file
     */
    void unloadExtensionAsync(@NotNull("Extension File can't be null") File jarFile);

    /**
     * Un load extension.
     *
     * @param jarFile the jar file
     */
    void unloadExtension(@NotNull("Extension File can't be null") File jarFile);

    /**
     * Un load extension async.
     *
     * @param extensionName the extension name
     */
    void unloadExtensionAsync(@NotNull("Extension name can't be null") String extensionName);

    /**
     * Un load extension.
     *
     * @param extensionName the extension name
     */
    void unloadExtension(@NotNull("Extension name can't be null") String extensionName);

    /**
     * Un load extension async.
     *
     * @param extensionData the extension
     */
    void unloadExtensionAsync(@NotNull("Extension can't be null") ExtensionData extensionData);

    /**
     * Un load extension.
     *
     * @param extensionData the extension
     */
    void unloadExtension(@NotNull ExtensionData extensionData);
}
