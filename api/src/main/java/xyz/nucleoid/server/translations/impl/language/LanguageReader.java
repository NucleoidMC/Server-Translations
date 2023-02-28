package xyz.nucleoid.server.translations.impl.language;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import xyz.nucleoid.server.translations.api.language.ServerLanguageDefinition;
import xyz.nucleoid.server.translations.impl.ServerTranslations;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public final class LanguageReader {
    public static TranslationMap read(InputStream stream) {
        TranslationMap map = new TranslationMap();
        Language.load(stream, map::put);
        return map;
    }

    public static TranslationMap loadBuiltInTranslation() {
        try (InputStream input = Language.class.getResourceAsStream("/assets/minecraft/lang/" + ServerLanguageDefinition.DEFAULT_CODE + ".json")) {
            return LanguageReader.read(input);
        } catch (IOException e) {
            ServerTranslations.LOGGER.warn("Failed to load default language", e);
            return new TranslationMap();
        }
    }

    public static Multimap<String, Supplier<TranslationMap>> collectDataPackTranslations(ResourceManager manager) {
        Multimap<String, Supplier<TranslationMap>> translationSuppliers = HashMultimap.create();

        for (Identifier path : manager.findResources("lang", path -> path.getPath().endsWith(".json")).keySet()) {
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
