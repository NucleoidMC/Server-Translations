package fr.catcore.translated.server.resource.language;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ServerLanguageManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ServerLanguageDefinition en_us = new ServerLanguageDefinition("en_us", "US", "English", false);
    private String currentLanguageCode;
    private Map<String, ServerLanguageDefinition> languageDefs;
    private ServerLanguageDefinition currentLanguage;
    private static ServerLanguageManager instance;

    public ServerLanguageManager() {
        this.languageDefs = new HashMap<>();
        this.languageDefs.put("en_us", en_us);
        this.currentLanguage = en_us;
        this.currentLanguageCode = "en_us";
    }

    public static ServerLanguageManager getInstance() {
        if (instance == null) {
            instance = new ServerLanguageManager();
        }
        return instance;
    }

    public static void registerLanguageDefinition(String code, JsonObject jsonObject) {
        getInstance().languageDefs.putIfAbsent(code.toLowerCase(), new ServerLanguageDefinition(code, jsonObject.get("region").getAsString(),
                jsonObject.get("name").getAsString(), jsonObject.get("bidirectional").getAsBoolean()));
    }

    public void setLanguage(ServerLanguageDefinition languageDefinition) {
        this.currentLanguageCode = languageDefinition.getCode();
        this.currentLanguage = languageDefinition;
    }

    public ServerLanguageDefinition getLanguage() {
        return this.currentLanguage;
    }

    public SortedSet<ServerLanguageDefinition> getAllLanguages() {
        return Sets.newTreeSet(this.languageDefs.values());
    }

    public ServerLanguageDefinition getLanguage(String code) {
        return (ServerLanguageDefinition)this.languageDefs.getOrDefault(code, en_us);
    }
}
