package fr.catcore.server.translations;

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
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TranslationGatherer {

    private static final JsonParser PARSER = new JsonParser();
    private static final Path TEMP_PATH = FabricLoader.getInstance().getGameDir().resolve("temp/translations");
    private static final Path MODS_PATH = FabricLoader.getInstance().getGameDir().resolve("mods");

    public static void init() {
        try {
            VanillaAssets assets = VanillaAssets.get();

            Map<String, ServerLanguageDefinition> languages = loadLanguageDefinitions(assets);
            for (ServerLanguageDefinition language : languages.values()) {
                Supplier<LanguageMap> supplier = () -> loadVanillaLanguage(assets, language);
                ServerLanguageManager.INSTANCE.addTranslations(language, supplier);
            }

//            lookIntoModFiles(languages);
        } catch (Exception e) {
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

    private static void lookIntoModFiles(Map<String, ServerLanguageDefinition> languages) throws IOException {
        Files.walk(MODS_PATH, 1).forEach(mod -> {
            try {
                if(Files.isDirectory(mod) || !mod.getFileName().endsWith(".jar") || !isZip(mod)) return;
                readFromJar(mod, languages);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        FileUtils.deleteDirectory(TEMP_PATH.toFile());
    }

    private static void readFromJar(Path path, Map<String, ServerLanguageDefinition> languages) throws IOException {
        try (JarFile jarFile = new JarFile(path.toFile())) {
            List<JarEntry> langEntries = new ArrayList<>();

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry ze = entries.nextElement();
                String entryName = ze.getName();
                if (entryName.startsWith("assets") && entryName.contains("lang")) {
                    if (!ze.isDirectory()) langEntries.add(ze);
                }
            }

            for (JarEntry langEntry : langEntries) {
                String entryName = langEntry.getName();
                ServerLanguageDefinition language = null;

                if (entryName.contains(".json")) {
                    String langCode = entryName.split("lang")[1].replace("/", "").replace(".json", "");
                    language = languages.get(langCode);
                } else if (entryName.contains(".lang")) {
                    String langCode = entryName.split("lang")[1].replace("/", "").replace(".lang", "").toLowerCase(Locale.ROOT);
                    language = languages.get(langCode);
                }

                if (language != null) {
                    ServerLanguageManager.INSTANCE.addTranslations(language, () -> {
                        try {
                            return readFromJar(path, entryName);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    });
                }
            }
        }
    }

    private static LanguageMap readFromJar(Path jarPath, String name) throws IOException {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            JarEntry entry = jarFile.getJarEntry(name);
            if (entry != null) {
                InputStream input = jarFile.getInputStream(entry);
                if (name.endsWith(".json")) {
                    return LanguageReader.read(input);
                } else {
                    return LanguageReader.readLegacy(input);
                }
            }
        }
        return null;
    }

    public static boolean isZip(Path path) {
        try (RandomAccessFile rfile = new RandomAccessFile(path.toFile(), "r")) {
            long n = rfile.readInt();
            rfile.close();
            return n == 0x504B0304;
            // 504B0304 is a magic number (the file signature) for .zip and thus .jar files https://en.wikipedia.org/wiki/List_of_file_signatures
        } catch (IOException e) {
            return false;
        }
    }
}
