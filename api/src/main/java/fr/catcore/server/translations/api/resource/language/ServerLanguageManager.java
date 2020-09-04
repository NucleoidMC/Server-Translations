package fr.catcore.server.translations.api.resource.language;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class ServerLanguageManager implements ResourceReloadListener {
    public static final String DEFAULT_CODE = "en_us";
    public static final ServerLanguageDefinition DEFAULT = new ServerLanguageDefinition(DEFAULT_CODE, "US", "English", false);

    private static final Logger LOGGER = LogManager.getLogger(ServerLanguageManager.class);

    public static final ServerLanguageManager INSTANCE = new ServerLanguageManager();

    private final Multimap<String, Supplier<LanguageMap>> languageSuppliers = HashMultimap.create();

    private final SortedMap<String, ServerLanguageDefinition> supportedLanguages = Maps.newTreeMap();
    private final Map<String, ServerLanguage> languages = new HashMap<>();

    private ServerLanguage systemLanguage;

    private final List<TranslationsReloadListener> reloadListeners = new ArrayList<>();

    private ServerLanguageManager() {
        this.loadSupportedLanguages();
        this.addTranslations(DEFAULT_CODE, ServerLanguageManager::loadDefaultLanguage);

        this.systemLanguage = this.createLanguage(DEFAULT);
    }

    private static LanguageMap loadDefaultLanguage() {
        LanguageMap translations = new LanguageMap();

        try (InputStream input = Language.class.getResourceAsStream("/assets/minecraft/lang/" + DEFAULT.getCode() + ".json")) {
            Language.load(input, translations::put);
        } catch (IOException e) {
            LOGGER.warn("Failed to load default langage", e);
        }

        return translations;
    }

    private void loadSupportedLanguages() {
        try {
            List<ServerLanguageDefinition> definitions = ServerLanguageDefinition.loadLanguageDefinitions();
            for (ServerLanguageDefinition language : definitions) {
                this.supportedLanguages.put(language.getCode(), language);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load server language definitions", e);
        }
    }

    public void addTranslations(String code, Supplier<LanguageMap> supplier) {
        ServerLanguage language = this.languages.get(code);
        if (language != null) {
            LanguageMap map = supplier.get();
            if (map != null) {
                language.putAll(map);
            }
        } else {
            this.languageSuppliers.put(code, supplier);
        }
    }

    private void clearTranslations() {
        this.languageSuppliers.clear();
        this.languages.values().forEach(ServerLanguage::clearTranslations);
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

        // we add all the default translation keys to other languages
        if (!definition.equals(DEFAULT)) {
            ServerLanguage defaultLanguage = this.getDefaultLanguage();
            language.putAll(defaultLanguage.getMap());
        }

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

    public ServerLanguage getDefaultLanguage() {
        return this.languages.get(DEFAULT_CODE);
    }

    public Iterable<ServerLanguageDefinition> getAllLanguages() {
        return this.supportedLanguages.values();
    }

    public void registerReloadListener(TranslationsReloadListener reloadListener) {
        this.reloadListeners.add(reloadListener);
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        CompletableFuture<Multimap<String, Supplier<LanguageMap>>> future = CompletableFuture.supplyAsync(() -> {
            this.clearTranslations();
            this.addTranslations(DEFAULT_CODE, ServerLanguageManager::loadDefaultLanguage);
            this.reloadListeners.forEach(TranslationsReloadListener::reload);

            return this.collectLanguageSuppliers(manager);
        });

        return future.thenCompose(synchronizer::whenPrepared)
                .thenAcceptAsync(v -> {
                    Multimap<String, Supplier<LanguageMap>> languageSuppliers = future.join();
                    languageSuppliers.forEach(this::addTranslations);

                    int keyCount = ServerLanguageManager.INSTANCE.getSystemLanguage().getKeyCount();
                    LOGGER.info(new TranslatableText("text.translated_server.loaded.translation_key", String.valueOf(keyCount)).getString());
                });
    }

    private Multimap<String, Supplier<LanguageMap>> collectLanguageSuppliers(ResourceManager manager) {
        Multimap<String, Supplier<LanguageMap>> languageSuppliers = HashMultimap.create();

        for (Identifier path : manager.findResources("lang", path -> path.endsWith(".json"))) {
            String code = this.getLanguageCodeForPath(path);

            languageSuppliers.put(code, () -> {
                LanguageMap map = new LanguageMap();
                try {
                    for (Resource resource : manager.getAllResources(path)) {
                        map.putAll(LanguageReader.read(resource.getInputStream()));
                    }
                } catch (RuntimeException | IOException e) {
                    LOGGER.warn("Failed to load language resource at {}", path, e);
                }
                return map;
            });
        }

        return languageSuppliers;
    }

    private String getLanguageCodeForPath(Identifier file) {
        String path = file.getPath();
        return path.substring("lang".length() + 1, path.length() - ".json".length());
    }
}
