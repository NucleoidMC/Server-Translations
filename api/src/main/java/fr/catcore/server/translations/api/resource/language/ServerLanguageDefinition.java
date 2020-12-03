package fr.catcore.server.translations.api.resource.language;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.catcore.server.translations.api.ServerTranslations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ServerLanguageDefinition implements Comparable<ServerLanguageDefinition> {
    public static final String DEFAULT_CODE = "en_us";
    public static final ServerLanguageDefinition DEFAULT = new ServerLanguageDefinition(DEFAULT_CODE, "US", "English", false);

    private static final JsonParser PARSER = new JsonParser();

    private final String code;
    private final String name;
    private final String region;
    private final boolean rightToLeft;

    public ServerLanguageDefinition(String code, String name, String region, boolean rightToLeft) {
        this.code = code;
        this.name = name;
        this.region = region;
        this.rightToLeft = rightToLeft;
    }

    public String getCode() {
        return this.code;
    }

    public static List<ServerLanguageDefinition> loadLanguageDefinitions() throws IOException {
        List<ServerLanguageDefinition> languageDefinitions = new ArrayList<>();

        try (BufferedReader read = new BufferedReader(new InputStreamReader(ServerTranslations.class.getResourceAsStream("/minecraft_languages.json")))) {
            JsonObject root = PARSER.parse(read).getAsJsonObject().getAsJsonObject("language");

            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                ServerLanguageDefinition definition = ServerLanguageDefinition.parse(entry.getKey(), (JsonObject) entry.getValue());
                languageDefinitions.add(definition);
            }
        }

        return languageDefinitions;
    }

    public static ServerLanguageDefinition parse(String code, JsonObject jsonObject) {
        String region = jsonObject.get("region").getAsString();
        String name = jsonObject.get("name").getAsString();
        boolean bidirectional = jsonObject.get("bidirectional").getAsBoolean();
        return new ServerLanguageDefinition(code.toLowerCase(Locale.ROOT), region, name, bidirectional);
    }

    public String getName() {
        return this.region;
    }

    public String getRegion() {
        return this.name;
    }

    public boolean isRightToLeft() {
        return this.rightToLeft;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", this.region, this.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            return o instanceof ServerLanguageDefinition && this.code.equals(((ServerLanguageDefinition) o).code);
        }
    }

    @Override
    public int hashCode() {
        return this.code.hashCode();
    }

    @Override
    public int compareTo(ServerLanguageDefinition languageDefinition) {
        return this.code.compareTo(languageDefinition.code);
    }
}
