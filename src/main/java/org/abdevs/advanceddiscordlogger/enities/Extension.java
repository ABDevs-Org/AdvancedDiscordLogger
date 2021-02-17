package org.abdevs.advanceddiscordlogger.enities;


import com.google.gson.JsonObject;
import org.abdevs.advanceddiscordlogger.api.IExtension;

import java.io.File;

public class Extension {
    private final String name;
    private final String version;
    private final String description;
    private final String author;
    private final IExtension instance;
    private final File jarFile;
    private final JsonObject configObject;

    public Extension(String name, String version, String description, String author, IExtension instance, File jarFile, JsonObject configObject) {
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

    public IExtension getInstance() {
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
