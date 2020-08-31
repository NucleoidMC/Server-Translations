package fr.catcore.server.translations.api.resource.language;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class ServerLanguageManager implements ResourceReloadListener {

    private static final JsonParser PARSER = new JsonParser();

    public static final ServerLanguageDefinition DEFAULT = new ServerLanguageDefinition("en_us", "US", "English", false);
    public static final ServerLanguageManager INSTANCE = new ServerLanguageManager();

    private static final Logger LOGGER = LogManager.getLogger(ServerLanguageManager.class);
    private static final Gson GSON = new Gson();

    private final Multimap<String, Supplier<LanguageMap>> languageSuppliers = HashMultimap.create();

    private final Map<String, ServerLanguage> languages = new HashMap<>();
    private final SortedMap<String, ServerLanguageDefinition> supportedLanguages = Maps.newTreeMap();
    private ServerLanguage systemLanguage;

    private static final List<TranslationsReloadListener> RELOAD_START_LISTENERS = new ArrayList<>();

    private static List<String> VANILLA_KEYS;

    private ServerLanguageManager() {
        VANILLA_KEYS = new ArrayList<>();
        this.systemLanguage = this.createLanguage(DEFAULT);
        this.addTranslations(DEFAULT, ServerLanguageManager::loadDefaultLanguage);
        this.getMinecraftLanguageList();
    }

    private static LanguageMap loadDefaultLanguage() {
        LanguageMap translations = new LanguageMap();

        try (InputStream input = Language.class.getResourceAsStream("/assets/minecraft/lang/" + DEFAULT.getCode() + ".json")) {
            Language.load(input, translations::put);
        } catch (IOException e) {
            LOGGER.warn("Failed to load default langage", e);
        }

        for (Map.Entry<String, String> entry : translations.entrySet()) {
            VANILLA_KEYS.add(entry.getKey());
        }
        return translations;
    }

    private void getMinecraftLanguageList() {
        Map<String, ServerLanguageDefinition> languageDefinitions = new HashMap<>();
        try(BufferedReader read = new BufferedReader(new InputStreamReader(ServerLanguage.class.getResourceAsStream("/minecraft_languages.json")))) {
            JsonObject jsonObject = PARSER.parse(read).getAsJsonObject().getAsJsonObject("language");

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                ServerLanguageDefinition definition = ServerLanguageDefinition.parse(entry.getKey(), (JsonObject) entry.getValue());
                languageDefinitions.putIfAbsent(definition.getCode(), definition);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (ServerLanguageDefinition language : languageDefinitions.values()) {
            LanguageMap map = new LanguageMap();
            if (language != null) {
                this.addTranslations(language, () -> map);
            }
        }
    }

    public void addTranslations(ServerLanguageDefinition definition, LanguageMap map) {
        this.addTranslations(definition, () -> map);
    }

    public void addTranslations(ServerLanguageDefinition definition, Supplier<LanguageMap> supplier) {
        this.supportedLanguages.putIfAbsent(definition.getCode(), definition);

        ServerLanguage language = this.languages.get(definition.getCode());
        if (language != null) {
            LanguageMap map = supplier.get();
            if (map != null) {
                language.putAll(map);
            }
        } else {
            this.languageSuppliers.put(definition.getCode(), supplier);
        }
    }

    private void clearTranslations() {
        this.languageSuppliers.clear();
        this.languages.forEach((s, serverLanguage) -> {
            serverLanguage.clearTranslations();
        });
    }

    public ServerLanguage getLanguage(String code) {
        if (code == null) {
            return this.systemLanguage;
        }

        ServerLanguage language = this.languages.get(code);
        if (language != null) {
            return language;
        }

        ServerLanguageDefinition definition = this.supportedLanguages.get(code);
        if (definition != null) {
            return this.createLanguage(definition);
        } else {
            return this.systemLanguage;
        }
    }

    private ServerLanguage createLanguage(ServerLanguageDefinition definition) {
        ServerLanguage language = new ServerLanguage(definition);
        this.languages.put(definition.getCode(), language);

        Collection<Supplier<LanguageMap>> suppliers = this.languageSuppliers.removeAll(definition.getCode());
        for (Supplier<LanguageMap> supplier : suppliers) {
            LanguageMap map = supplier.get();
            if (map != null) {
                language.putAll(map);
            }
        }

        return language;
    }

    public void setSystemLanguage(ServerLanguageDefinition definition) {
        this.systemLanguage = this.getLanguage(definition.getCode());
    }

    public ServerLanguage getSystemLanguage() {
        return this.systemLanguage;
    }

    public Iterable<ServerLanguageDefinition> getAllLanguages() {
        return this.supportedLanguages.values();
    }

    public void registerReloadStartListener(TranslationsReloadListener reloadListener) {
        RELOAD_START_LISTENERS.add(reloadListener);
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        CompletableFuture<Map<Identifier, LanguageMap>> completableFuture = CompletableFuture.supplyAsync(() -> {
            this.clearTranslations();
            this.addTranslations(DEFAULT, ServerLanguageManager::loadDefaultLanguage);
            RELOAD_START_LISTENERS.forEach(TranslationsReloadListener::reload);
            Map<Identifier, LanguageMap> map = Maps.newHashMap();
            for (Identifier identifier : manager.findResources("lang", (stringx) -> {
                return stringx.endsWith(".json");
            })) {
                String string = identifier.getPath();
                Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring("lang".length() + 1, string.length() - ".json".length()));

                try {
                    for (Resource resource : manager.getAllResources(identifier)) {

                        try {
                            InputStream inputStream = resource.getInputStream();
                            Throwable var10 = null;

                            map.putIfAbsent(identifier2, LanguageReader.read(inputStream));
                        } catch (RuntimeException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return map;
        });
        CompletableFuture<Void> completableFuture1 = completableFuture.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((void_) -> {
            Map<Identifier, LanguageMap> map = completableFuture.join();
            for (Map.Entry<Identifier, LanguageMap> entry : map.entrySet()) {
                ServerLanguageDefinition languageDefinition = this.getLanguage(entry.getKey().getPath()).getDefinition();
                this.addTranslations(languageDefinition, entry.getValue());
            }
            ServerLanguage serverLanguage = this.getLanguage("en_us");
            for (ServerLanguageDefinition serverLanguageDefinition : this.getAllLanguages()) {
                ServerLanguage serverLanguage1 = this.getLanguage(serverLanguageDefinition.getCode());
                LanguageMap map1 = new LanguageMap();
                for (Map.Entry<String, String> entry : serverLanguage.getEntryList()) {
                    if (!serverLanguage1.hasTranslation(entry.getKey())) {
                        if (!VANILLA_KEYS.contains(entry.getKey())) {
                            map1.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                this.addTranslations(serverLanguageDefinition, () -> map1);
            }
            int keyNumber = ServerLanguageManager.INSTANCE.getSystemLanguage().getKeyNumber();
            LOGGER.info(new TranslatableText("text.translated_server.loaded.translation_key", String.valueOf(keyNumber)).getString());
        });
        return completableFuture1;
    }
}
