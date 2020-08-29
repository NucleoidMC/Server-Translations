package fr.catcore.server.translations.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.catcore.server.translations.api.resource.language.LanguageMap;
import fr.catcore.server.translations.api.resource.language.LanguageReader;
import fr.catcore.server.translations.api.resource.language.ServerLanguageDefinition;
import fr.catcore.server.translations.api.resource.language.ServerLanguageManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.util.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class ServerTranslationsAPI implements ModInitializer {

    private static final JsonParser PARSER = new JsonParser();

    @Override
    public void onInitialize() {
        init();
        ServerLanguageManager.INSTANCE.registerReloadStartListener(ServerTranslationsAPI::init);
    }

    private static void init() {
        try {
            VanillaAssets assets = VanillaAssets.get();
            Map<String, ServerLanguageDefinition> languages = loadLanguageDefinitions(assets);
            for (ServerLanguageDefinition language : languages.values()) {
                Supplier<LanguageMap> supplier = () -> loadVanillaLanguage(assets, language);
                ServerLanguageManager.INSTANCE.addTranslations(language, supplier);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, ServerLanguageDefinition> loadLanguageDefinitions(VanillaAssets assets) throws IOException {
        Map<String, ServerLanguageDefinition> languageDefinitions = new HashMap<>();

        try (BufferedReader read = new BufferedReader(new InputStreamReader(assets.openStream("pack.mcmeta")))) {
            JsonObject jsonObject = PARSER.parse(read).getAsJsonObject().getAsJsonObject("language");

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                ServerLanguageDefinition definition = ServerLanguageDefinition.parse(entry.getKey(), (JsonObject) entry.getValue());
                languageDefinitions.putIfAbsent(definition.getCode(), definition);
            }
        }

        return languageDefinitions;
    }

    private static LanguageMap loadVanillaLanguage(VanillaAssets assets, ServerLanguageDefinition language) {
        try {
            ModContainer vanilla = FabricLoader.getInstance().getModContainer("minecraft").get();
            SemanticVersion minecraftVersion = SemanticVersion.parse(vanilla.getMetadata().getVersion().getFriendlyString());

            if (minecraftVersion.compareTo(SemanticVersion.parse("1.13-Snapshot.17.48.a")) >= 0) {
                InputStream stream;
                if (ServerLanguageManager.DEFAULT.equals(language)) {
                    stream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
                } else {
                    stream = assets.openStream("minecraft/lang/" + language.getCode() + ".json");
                }
                return LanguageReader.read(stream);
            } else {
                InputStream stream;
                if (minecraftVersion.compareTo(SemanticVersion.parse("1.11-Snapshot.16.32.a")) >= 0) {
                    if (ServerLanguageManager.DEFAULT.equals(language)) {
                        stream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.lang");
                    } else {
                        stream = assets.openStream("minecraft/lang/" + language.getCode() + ".lang");
                    }
                } else {
                    if (ServerLanguageManager.DEFAULT.equals(language)) {
                        stream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang");
                    } else {
                        stream = assets.openStream("minecraft/lang/" + language.getCode().split("_")[0] + "_" + language.getCode().split("_")[1].toUpperCase(Locale.ROOT) + ".lang");
                    }
                }

                return LanguageReader.readLegacy(stream);
            }
        } catch (VersionParsingException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static URL getLanguageURL(String code) {
        try {
            return new URL("https://github.com/arthurbambou/Server-Translations/raw/master/src/main/resources/data/server_translations/lang/" + code + ".json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
