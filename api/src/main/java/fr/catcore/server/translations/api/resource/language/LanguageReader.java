package fr.catcore.server.translations.api.resource.language;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fr.catcore.server.translations.api.ServerTranslations;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Supplier;

public final class LanguageReader {
    public static TranslationMap read(InputStream stream) {
        TranslationMap map = new TranslationMap();
        Language.load(stream, map::put);
        return map;
    }

    public static TranslationMap readLegacy(InputStream input) {
        TranslationMap map = new TranslationMap();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        reader.lines().forEach(line -> {
            if (line.startsWith("\n") || line.startsWith("#") || line.startsWith("/")) {
                return;
            }

            String key = line.split("=")[0];
            int values = line.split("=").length;

            StringBuilder value = new StringBuilder();
            for (int i = 1; i < values; i++) {
                value.append(line.split("=")[i]);
            }

            map.put(key, value.toString());
        });

        return map;
    }

    public static TranslationMap loadVanillaTranslations() {
        try (InputStream input = Language.class.getResourceAsStream("/assets/minecraft/lang/" + ServerLanguageDefinition.DEFAULT_CODE + ".json")) {
            return LanguageReader.read(input);
        } catch (IOException e) {
            ServerTranslations.LOGGER.warn("Failed to load default language", e);
            return new TranslationMap();
        }
    }

    public static Multimap<String, Supplier<TranslationMap>> collectTranslationSuppliers(ResourceManager manager) {
        Multimap<String, Supplier<TranslationMap>> translationSuppliers = HashMultimap.create();

        for (Identifier path : manager.findResources("lang", path -> path.endsWith(".json"))) {
            String code = getLanguageCodeForPath(path);

            translationSuppliers.put(code, () -> {
                TranslationMap map = new TranslationMap();
                try {
                    for (Resource resource : manager.getAllResources(path)) {
                        map.putAll(read(resource.getInputStream()));
                    }
                } catch (RuntimeException | IOException e) {
                    ServerTranslations.LOGGER.warn("Failed to load language resource at {}", path, e);
                }
                return map;
            });
        }

        return translationSuppliers;
    }

    private static String getLanguageCodeForPath(Identifier file) {
        String path = file.getPath();
        path = path.substring("lang".length() + 1, path.length() - ".json".length());

        return ServerTranslations.INSTANCE.getCodeAlias(path);
    }
}
