package org.abdevs.advanceddiscordlogger.enities;


import com.google.gson.JsonObject;
import org.abdevs.advanceddiscordlogger.api.base.Extension;

import java.io.File;

public class ExtensionData {
    private final String name;
    private final String version;
    private final String description;
    private final String author;
    private final Extension instance;
    private final File jarFile;
    private final JsonObject configObject;

    public ExtensionData(String name, String version, String description, String author, Extension instance, File jarFile, JsonObject configObject) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.author = author;
        this.instance = instance;
        this.jarFile = jarFile;
        this.configObject = configObject;
    }

    public JsonObject getConfigObject() {
        return configObject;
    }

    public File getJarFile() {
        return jarFile;
    }

    public Extension getInstance() {
        return instance;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }
}
