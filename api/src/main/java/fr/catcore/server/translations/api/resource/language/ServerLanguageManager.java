package fr.catcore.server.translations.api.resource.language;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.function.Supplier;

public class ServerLanguageManager {
    public static final ServerLanguageDefinition DEFAULT = new ServerLanguageDefinition("en_us", "US", "English", false);
    public static final ServerLanguageManager INSTANCE = new ServerLanguageManager();

    private static final Logger LOGGER = LogManager.getLogger(ServerLanguageManager.class);

    private final Multimap<String, Supplier<LanguageMap>> languageSuppliers = HashMultimap.create();

    private final Map<String, ServerLanguage> languages = new HashMap<>();
    private final SortedMap<String, ServerLanguageDefinition> supportedLanguages = Maps.newTreeMap();
    private ServerLanguage systemLanguage;

    private ServerLanguageManager() {
        this.systemLanguage = this.createLanguage(DEFAULT);
        this.addTranslations(DEFAULT, ServerLanguageManager::loadDefaultLanguage);
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

    public ServerLanguage getLanguage(String code) {
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
}
