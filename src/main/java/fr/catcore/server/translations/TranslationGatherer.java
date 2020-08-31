package fr.catcore.server.translations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.catcore.server.translations.api.resource.language.LanguageMap;
import fr.catcore.server.translations.api.resource.language.LanguageReader;
import fr.catcore.server.translations.api.resource.language.ServerLanguageDefinition;
import fr.catcore.server.translations.api.resource.language.ServerLanguageManager;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.util.Language;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

public class TranslationGatherer {

    private static final JsonParser PARSER = new JsonParser();
    private static final URL LANGUAGE_LIST;

    static {
        URL languageList;
        try {
            languageList = new URL("https://github.com/arthurbambou/Server-Translations/raw/master/src/main/resources/data/server_translations/language_list.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            languageList = null;
        }
        LANGUAGE_LIST = languageList;
    }

    public static void init() {
        try {
            VanillaAssets assets = VanillaAssets.get();
            Iterable<ServerLanguageDefinition> languages = ServerLanguageManager.INSTANCE.getAllLanguages();
            for (ServerLanguageDefinition language : languages) {
                Supplier<LanguageMap> supplier = () -> loadVanillaLanguage(assets, language);
                ServerLanguageManager.INSTANCE.addTranslations(language, supplier);
            }
            getModTranslationFromGithub(languages);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private static void getModTranslationFromGithub(Iterable<ServerLanguageDefinition> languages) {
        try (BufferedReader read = new BufferedReader(new InputStreamReader(LANGUAGE_LIST.openStream()))) {
            JsonObject jsonObject = PARSER.parse(read).getAsJsonObject();
            JsonArray languageArray = jsonObject.getAsJsonArray("languages");

            for (Iterator<JsonElement> it = languageArray.iterator(); it.hasNext(); ) {
                JsonElement entry = it.next();

                String code = entry.getAsString();
                URL languageURL = getLanguageURL(code);
                if (languageURL == null) continue;
                for (ServerLanguageDefinition languageDefinition : languages) {
                    if (languageDefinition.getCode().equals(code)) {
                        ServerLanguageManager.INSTANCE.addTranslations(languageDefinition, LanguageReader.read(languageURL.openStream()));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
