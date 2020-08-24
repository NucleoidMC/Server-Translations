package fr.catcore.server.translations;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.catcore.server.translations.resource.language.ServerLanguage;
import fr.catcore.server.translations.resource.language.ServerLanguageDefinition;
import fr.catcore.server.translations.resource.language.ServerLanguageManager;
import fr.catcore.server.translations.resource.language.ServerTranslationStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;
import net.minecraft.MinecraftVersion;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class TranslationGatherer {

    private static final Gson GSON = new Gson();
    private static URL versionURL = null;
    private static URL resourceManifestURL = null;
    private static final Map<String, String> resourceHashs = new HashMap<>();
    private static final Map<String, Map<String, String>> translations = new HashMap<>();

    public static void init() {
        try {
            getVersionURL();
            getResourceManifestURL();
            getVanillaResourceList();
            loadVanillaMCMeta();
            loadVanillaTranslations();
            lookIntoModFiles();
            addMissingEntryToOtherLanguage();
        } catch (Exception malformedURLException) {
            malformedURLException.printStackTrace();
        }
    }

    private static void getVersionURL() throws IOException {
        URL manifestURL = new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
        BufferedReader read = new BufferedReader(new InputStreamReader(manifestURL.openStream()));
        JsonObject jsonObject = GSON.fromJson(read, JsonObject.class);
        JsonArray versions = jsonObject.getAsJsonArray("versions");
        Iterator<JsonElement> iterator = versions.iterator();
        List<JsonObject> versionObjects = new ArrayList<>();
        while (iterator.hasNext()) {
            versionObjects.add((JsonObject) iterator.next());
        }
        for (JsonObject version : versionObjects) {
            try {
                if (version.get("id").getAsString().equals(MinecraftVersion.field_25319.getName())) {
                    versionURL = new URL(version.get("url").getAsString());
                }
            } catch (NoSuchFieldError error) {
                if (version.get("id").getAsString().equals(MinecraftVersion.create().getName())) {
                    versionURL = new URL(version.get("url").getAsString());
                }
            } catch (NoClassDefFoundError error) {
                if (version.get("id").getAsString().equals(FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString())) {
                    versionURL = new URL(version.get("url").getAsString());
                }
            }
        }
        read.close();
    }

    private static void getResourceManifestURL() throws IOException {
        BufferedReader read = new BufferedReader(new InputStreamReader(versionURL.openStream()));
        JsonObject jsonObject = GSON.fromJson(read, JsonObject.class);
        resourceManifestURL = new URL(jsonObject.getAsJsonObject("assetIndex").get("url").getAsString());
        read.close();
    }

    private static void getVanillaResourceList() throws IOException {
        BufferedReader read = new BufferedReader(new InputStreamReader(resourceManifestURL.openStream()));
        JsonObject jsonObject = GSON.fromJson(read, JsonObject.class);
        JsonObject resourceArray = jsonObject.getAsJsonObject("objects");

        for (Map.Entry<String, JsonElement> entry : resourceArray.entrySet()) {
            if (entry.getKey().equals("pack.mcmeta") || entry.getKey().startsWith("minecraft/lang/")) {
                resourceHashs.put(entry.getKey(), entry.getValue().getAsJsonObject().get("hash").getAsString());
            }
        }
        read.close();
    }

    private static void loadVanillaMCMeta() throws IOException {
        BufferedReader read = new BufferedReader(new InputStreamReader(getResourceURL(resourceHashs.get("pack.mcmeta")).openStream()));
        JsonObject jsonObject = GSON.fromJson(read, JsonObject.class).getAsJsonObject("language");

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            ServerLanguageManager.registerLanguageDefinition(entry.getKey(), (JsonObject) entry.getValue());
        }
        read.close();
    }

    private static void loadVanillaTranslations() throws IOException {
        for (ServerLanguageDefinition language : ServerLanguageManager.getInstance().getAllLanguages()) {
            try {
                if (SemanticVersion.parse(FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString()).compareTo(SemanticVersion.parse("1.13-Snapshot.17.48.a")) >= 0) {
                    if (language == ServerLanguageManager.getInstance().getLanguage("en_us")) {
                        BufferedReader read = new BufferedReader(new InputStreamReader(Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json")));
                        JsonObject jsonObject = GSON.fromJson(read, JsonObject.class);
                        Map<String, String> langTranslations = new HashMap<>();
                        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                            langTranslations.putIfAbsent(entry.getKey(), entry.getValue().getAsString());
                        }
                        translations.putIfAbsent(language.getCode(), langTranslations);
                        read.close();
                    } else {
                        BufferedReader read = new BufferedReader(new InputStreamReader(getResourceURL(resourceHashs.get("minecraft/lang/" + language.getCode() + ".json")).openStream()));
                        JsonObject jsonObject = GSON.fromJson(read, JsonObject.class);
                        Map<String, String> langTranslations = new HashMap<>();
                        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                            langTranslations.putIfAbsent(entry.getKey(), entry.getValue().getAsString());
                        }
                        translations.putIfAbsent(language.getCode(), langTranslations);
                        read.close();
                    }
                } else {
                    if (SemanticVersion.parse(FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString()).compareTo(SemanticVersion.parse("1.11-Snapshot.16.32.a")) >= 0) {
                        if (language == ServerLanguageManager.getInstance().getLanguage("en_us")) {
                            BufferedReader read = new BufferedReader(new InputStreamReader(Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.lang")));
                            readOldLangFiles("en_us", read);
                        } else {
                            BufferedReader read = new BufferedReader(new InputStreamReader(getResourceURL(resourceHashs.get("minecraft/lang/" + language.getCode() +".lang")).openStream()));
                            readOldLangFiles(language.getCode(), read);
                        }
                    } else {
                        if (language == ServerLanguageManager.getInstance().getLanguage("en_us")) {
                            BufferedReader read = new BufferedReader(new InputStreamReader(Language.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang")));
                            readOldLangFiles("en_us", read);
                        } else {
                            BufferedReader read = new BufferedReader(new InputStreamReader(getResourceURL(resourceHashs.get("minecraft/lang/" + language.getCode().split("_")[0] + "_" + language.getCode().split("_")[1].toUpperCase() +".lang")).openStream()));
                            readOldLangFiles(language.getCode(), read);
                        }
                    }
                }
            } catch (VersionParsingException parsingException) {
                parsingException.printStackTrace();
            }
        }
    }

    private static void readOldLangFiles(String code, BufferedReader bufferedReader) {
        Iterator<String> lines = bufferedReader.lines().iterator();
        Map<String, String> langTranslations = new HashMap<>();
        while (lines.hasNext()) {

            String line = lines.next();
            if (line.startsWith("\n") || line.startsWith("#") || line.startsWith("/")) continue;
            String key = line.split("=")[0];
            int values = line.split("=").length;
            String value = "";
            for (int a = 1; a < values; a++) {
                value = value + line.split("=")[a];
            }
            langTranslations.put(key, value);
        }
        if (translations.containsKey(code)) {
            for (Map.Entry<String, String> entry : langTranslations.entrySet()) {
                translations.get(code).put(entry.getKey(), entry.getValue());
            }
        } else {
            translations.put(code, langTranslations);
        }
    }

    private static URL getResourceURL(String hash) throws MalformedURLException {
        return new URL("https://resources.download.minecraft.net/" + hash.substring(0,2) + "/" + hash);
    }

    private static void lookIntoModFiles() {
        File modPath;
        try {
            modPath = new File(FabricLoader.getInstance().getGameDir().toFile(), "mods");
        } catch (NoSuchMethodError error) {
            modPath = new File(FabricLoader.getInstance().getGameDirectory(), "mods");
        }
        for (File mod : Objects.requireNonNull(modPath.listFiles())) {
            try {
                if(mod.isDirectory() || !mod.getName().endsWith(".jar") || !isZip(mod)) continue;
                JarFile jarFile = new JarFile(mod);
                List<ZipEntry> jarEntries = new ArrayList<>();
                Iterator<JarEntry> zip = jarFile.stream().iterator();
                List<ZipEntry> jarInJar = new ArrayList<>();
                while(zip.hasNext()) {
                    ZipEntry ze = zip.next();
                    String entryName = ze.getName();
                    if (entryName.startsWith("assets") && entryName.contains("lang")) {
                        if (!ze.isDirectory()) jarEntries.add(ze);
                    }
                    if (entryName.startsWith("META-INF") && entryName.contains("jars") && entryName.contains(".jar")) {
                        if (!ze.isDirectory()) jarInJar.add(ze);
                    }
                }
                for (ZipEntry zipEntry : jarEntries) {
                    if (zipEntry.getName().contains(".json")) {
                        String fileName = zipEntry.getName().split("lang")[1].replace("/", "").replace(".json", "");
                        BufferedReader read = new BufferedReader(new InputStreamReader(jarFile.getInputStream(zipEntry)));
                        JsonObject jsonObject = GSON.fromJson(read, JsonObject.class);
                        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                            translations.get(fileName).put(entry.getKey(), entry.getValue().getAsString());
                        }
                        read.close();
                    } else if (zipEntry.getName().contains(".lang")) {
                        String langCode = zipEntry.getName().split("lang")[1].replace("/", "").replace(".lang", "").toLowerCase();
                        BufferedReader read = new BufferedReader(new InputStreamReader(jarFile.getInputStream(zipEntry)));
                        readOldLangFiles(langCode, read);
                    }
                }
                for (ZipEntry zipEntry : jarInJar) {
                    lookIntoJarInJar(jarFile, zipEntry);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File tempDirectory = new File(FabricLoader.getInstance().getGameDirectory(), "temp/translations");
        if (tempDirectory.exists()) {
            if (!tempDirectory.delete()) {
                for (File file : tempDirectory.listFiles()) {
                    file.delete();
                }
                tempDirectory.delete();
            }
        }

    }

    private static void lookIntoJarInJar(JarFile rootJar, ZipEntry jar) throws IOException {
        InputStream inputStream = rootJar.getInputStream(jar);

        File file = new File(FabricLoader.getInstance().getGameDirectory(), "temp/translations/" + jar.getName().replace("META-INF/jars/", ""));
        if (!file.exists())new File(file.getParent()).mkdirs();
        Files.copy(
                inputStream,
                file.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        IOUtils.closeQuietly(inputStream);

        JarFile jarFile = new JarFile(file);
        List<ZipEntry> jarEntries = new ArrayList<>();
        Iterator<JarEntry> zip = jarFile.stream().iterator();
        List<ZipEntry> jarInJar = new ArrayList<>();
        while(zip.hasNext()) {
            ZipEntry ze = zip.next();
            String entryName = ze.getName();
            if (entryName.startsWith("assets") && entryName.contains("lang")) {
                if (!ze.isDirectory()) jarEntries.add(ze);
            }
            if (entryName.startsWith("META-INF") && entryName.contains("jars") && entryName.contains(".jar")) {
                if (!ze.isDirectory()) jarInJar.add(ze);
            }
        }
        for (ZipEntry zipEntry : jarEntries) {
            if (zipEntry.getName().contains(".json")) {
                String fileName = zipEntry.getName().split("lang")[1].replace("/", "").replace(".json", "");
                BufferedReader read = new BufferedReader(new InputStreamReader(jarFile.getInputStream(zipEntry)));
                JsonObject jsonObject = GSON.fromJson(read, JsonObject.class);
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    translations.get(fileName).put(entry.getKey(), entry.getValue().getAsString());
                }
                read.close();
            } else if (zipEntry.getName().contains(".lang")) {
                String langCode = zipEntry.getName().split("lang")[1].replace("/", "").replace(".lang", "").toLowerCase();
                BufferedReader read = new BufferedReader(new InputStreamReader(jarFile.getInputStream(zipEntry)));
                readOldLangFiles(langCode, read);
            }
        }
        for (ZipEntry zipEntry : jarInJar) {
            lookIntoJarInJar(jarFile, zipEntry);
        }
        file.delete();
    }

    private static void addMissingEntryToOtherLanguage() {
        for (Map.Entry<String, String> entry : translations.get("en_us").entrySet()) {
            for (Map.Entry<String, Map<String, String>> entry1 : translations.entrySet()) {
                if (entry1.getKey().equals("en_us")) continue;
                translations.get(entry1.getKey()).putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
    }

    public static void setLanguage(String code) {
        ServerLanguage.setInstance(new ServerTranslationStorage(translations.getOrDefault(code, translations.get("en_us")), ServerLanguageManager.getInstance().getLanguage(code).isRightToLeft()));
        ServerLanguageManager.getInstance().setLanguage(ServerLanguageManager.getInstance().getLanguage(code));
    }

    public static boolean isZip(File file) throws IOException {
        RandomAccessFile rfile = new RandomAccessFile(file, "r");
        long n = rfile.readInt();
        rfile.close();
        return n == 0x504B0304;
        // 504B0304 is a magic number (the file signature) for .zip and thus .jar files https://en.wikipedia.org/wiki/List_of_file_signatures
    }

    public static LiteralText getTranslation(String languageCode, TranslatableText translatableText) {
        String original = ServerLanguageManager.getInstance().getLanguage().getCode();
        Style style = translatableText.getStyle();
        // TODO: find another way to do this.
        setLanguage(languageCode);
        String string = translatableText.getString();

        setLanguage(original);
        return (LiteralText) new LiteralText(string).setStyle(style);
    }
}
