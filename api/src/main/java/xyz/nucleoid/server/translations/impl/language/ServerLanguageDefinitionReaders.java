package xyz.nucleoid.server.translations.impl.language;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import xyz.nucleoid.server.translations.api.language.ServerLanguageDefinition;
import xyz.nucleoid.server.translations.impl.ServerTranslations;
import net.minecraft.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ServerLanguageDefinitionReaders {
    public static Pair<List<ServerLanguageDefinition>, Map<String, String>> loadLanguageDefinitions() throws IOException {
        List<ServerLanguageDefinition> languageDefinitions = new ArrayList<>();
        Map<String, String> aliasList = new HashMap<>();

        try (BufferedReader read = new BufferedReader(new InputStreamReader(ServerTranslations.class.getResourceAsStream("/minecraft_languages.json")))) {
            JsonObject root = JsonParser.parseReader(read).getAsJsonObject();
            JsonObject languageRoot = root.getAsJsonObject("language");

            for (Map.Entry<String, JsonElement> entry : languageRoot.entrySet()) {
                ServerLanguageDefinition definition = parse(entry.getKey(), (JsonObject) entry.getValue());
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
}
