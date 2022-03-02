package fr.catcore.server.translations.api.resource.language;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.catcore.server.translations.api.ServerTranslations;
import net.minecraft.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public record ServerLanguageDefinition(String code, String region, String name,
                                       boolean rightToLeft) implements Comparable<ServerLanguageDefinition> {
    public static final String DEFAULT_CODE = "en_us";
    public static final ServerLanguageDefinition DEFAULT = new ServerLanguageDefinition(DEFAULT_CODE, "US", "English", false);

    public static Pair<List<ServerLanguageDefinition>, Map<String, String>> loadLanguageDefinitions() throws IOException {
        List<ServerLanguageDefinition> languageDefinitions = new ArrayList<>();
        Map<String, String> aliasList = new HashMap<>();

        try (BufferedReader read = new BufferedReader(new InputStreamReader(ServerTranslations.class.getResourceAsStream("/minecraft_languages.json")))) {
            JsonObject root = JsonParser.parseReader(read).getAsJsonObject();
            JsonObject languageRoot = root.getAsJsonObject("language");

            for (Map.Entry<String, JsonElement> entry : languageRoot.entrySet()) {
                ServerLanguageDefinition definition = ServerLanguageDefinition.parse(entry.getKey(), (JsonObject) entry.getValue());
                languageDefinitions.add(definition);
            }

            JsonObject aliasRoot = root.getAsJsonObject("alias");

            for (Map.Entry<String, JsonElement> entry : aliasRoot.entrySet()) {
                aliasList.put(entry.getKey(), entry.getValue().getAsString());
            }
        }

        return new Pair(languageDefinitions, aliasList);
    }

    public static ServerLanguageDefinition parse(String code, JsonObject jsonObject) {
        String region = jsonObject.get("region").getAsString();
        String name = jsonObject.get("name").getAsString();
        boolean bidirectional = jsonObject.get("bidirectional").getAsBoolean();
        return new ServerLanguageDefinition(code.toLowerCase(Locale.ROOT), region, name, bidirectional);
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
            return o instanceof ServerLanguageDefinition languageDefinition && this.code.equals(languageDefinition.code);
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
