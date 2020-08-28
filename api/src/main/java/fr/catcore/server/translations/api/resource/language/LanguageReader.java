package fr.catcore.server.translations.api.resource.language;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public final class LanguageReader {
    private static final JsonParser PARSER = new JsonParser();

    public static LanguageMap read(InputStream stream) throws IOException {
        try (BufferedReader read = new BufferedReader(new InputStreamReader(stream))) {
            JsonObject jsonObject = PARSER.parse(read).getAsJsonObject();
            LanguageMap map = new LanguageMap();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                map.put(entry.getKey(), entry.getValue().getAsString());
            }
            return map;
        }
    }

    public static LanguageMap readLegacy(InputStream input) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            LanguageMap map = new LanguageMap();

            reader.lines().forEach(line -> {
                if (line.startsWith("\n") || line.startsWith("#") || line.startsWith("/")) {
                    return;
                }

                String key = line.split("=")[0];
                int values = line.split("=").length;

                StringBuilder value = new StringBuilder();
                for (int a = 1; a < values; a++) {
                    value.append(line.split("=")[a]);
                }

                map.put(key, value.toString());
            });

            return map;
        }
    }
}
