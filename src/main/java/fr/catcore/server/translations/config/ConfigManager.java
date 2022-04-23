package fr.catcore.server.translations.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {

    private static Config config;

    private static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "translated_server.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String getLanguageCodeFromConfig() {
        if (!configFile.exists()) {
            try {
                FileWriter fileWriter = new FileWriter(configFile);
                config = new Config();
                fileWriter.write(GSON.toJson(config));
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileReader fileReader = new FileReader(configFile);
                config = GSON.fromJson(fileReader, Config.class);
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return config.getLanguageCode();
    }


    public static class Config {

        private final String language_code;

        public Config(String language_code) {
            this.language_code = language_code;
        }

        public Config() {
            this("en_us");
        }

        public String getLanguageCode() {
            return language_code;
        }
    }
}
