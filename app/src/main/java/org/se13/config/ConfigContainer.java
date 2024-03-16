package org.se13.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
 * this class contains config value from ./config.json file
 */
public class ConfigContainer {
    public ConfigContainer() {
        try
        {
            Reader reader = new FileReader(this.CONFIG_FILE_LOCATION);
            Gson gson = new Gson();

            this.configs = gson.fromJson(reader, JsonObject.class);
        }
        catch (FileNotFoundException e) {
            // TODO: implement error handler
        }
    }

    /**
     * returns config value to integer value.
     * @param key config's name
     * @return config value from json file located ./config.json
     */
    public int getConfig(String key) {
        return this.configs.get(key).getAsInt();
    }

    private final String CONFIG_FILE_LOCATION = "./config.json";
    private JsonObject configs;
}
