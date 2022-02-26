package fr.catcore.server.translations.api;

import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import fr.catcore.server.translations.api.resource.language.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class ServerTranslations implements ResourceReloader {
    public static final String ID = "server_translations_api";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ServerTranslations INSTANCE = new ServerTranslations();

    private final SortedMap<String, ServerLanguageDefinition> supportedLanguages = new Object2ObjectRBTreeMap<>();

    private final TranslationStore translations = new TranslationStore();
    private final Map<String, ServerLanguage> languages = new Object2ObjectOpenHashMap<>();

    private TranslationMap vanillaTranslations;

    private ServerLanguage defaultLanguage;
    private ServerLanguage systemLanguage;

    private final List<TranslationsReloadListener> reloadListeners = new ArrayList<>();

    private ServerTranslations() {
        this.loadSupportedLanguages();
        this.reload();
    }

    private void loadSupportedLanguages() {
        try {
            List<ServerLanguageDefinition> definitions = ServerLanguageDefinition.loadLanguageDefinitions();
            for (ServerLanguageDefinition language : definitions) {
                this.supportedLanguages.put(language.code(), language);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load server language definitions", e);
        }
    }

    private void reload() {
        this.translations.clear();
        this.languages.clear();

        this.vanillaTranslations = LanguageReader.loadVanillaTranslations();
        this.translations.add(ServerLanguageDefinition.DEFAULT_CODE, () -> this.vanillaTranslations);

        this.defaultLanguage = this.createLanguage(ServerLanguageDefinition.DEFAULT);

        if (this.systemLanguage != null) {
            this.setSystemLanguage(this.systemLanguage.definition());
        } else {
            this.setSystemLanguage(ServerLanguageDefinition.DEFAULT);
        }

        this.reloadListeners.forEach(TranslationsReloadListener::reload);
    }

    public void addTranslations(String code, Supplier<TranslationMap> supplier) {
        this.translations.add(code, supplier);
    }

    @NotNull
    public ServerLanguage getLanguage(@Nullable LocalizationTarget target) {
        return this.getLanguage(target != null ? target.getLanguageCode() : null);
    }

    @NotNull
    public ServerLanguage getLanguage(@Nullable String code) {
        if (code == null) {
            return this.defaultLanguage;
        }

        ServerLanguage language = this.languages.get(code);
        if (language != null) {
            return language;
        }

        ServerLanguageDefinition definition = this.supportedLanguages.get(code);
        if (definition != null) {
            return this.createLanguage(definition);
        } else {
            return this.defaultLanguage;
        }
    }

    @NotNull
    public ServerLanguageDefinition getLanguageDefinition(@Nullable String code) {
        if (code == null) {
            return ServerLanguageDefinition.DEFAULT;
        }
        return this.supportedLanguages.getOrDefault(code, ServerLanguageDefinition.DEFAULT);
    }

    private ServerLanguage createLanguage(ServerLanguageDefinition definition) {
        TranslationMap translations = this.translations.get(definition.code());
        TranslationMap defaultTranslations = this.translations.get(ServerLanguageDefinition.DEFAULT_CODE);

        TranslationAccess remote = translations.union(defaultTranslations);
        TranslationAccess local = remote.subtract(this.vanillaTranslations);

        boolean isClient = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

        ServerLanguage language = new ServerLanguage(definition, isClient ? local : remote, isClient ? remote : local);
        this.languages.put(definition.code(), language);

        return language;
    }

    public void setSystemLanguage(ServerLanguageDefinition definition) {
        this.systemLanguage = Objects.requireNonNull(this.getLanguage(definition.code()));
    }

    @NotNull
    public ServerLanguage getSystemLanguage() {
        return this.systemLanguage;
    }

    @NotNull
    public ServerLanguage getDefaultLanguage() {
        return this.defaultLanguage;
    }

    public Iterable<ServerLanguageDefinition> getAllLanguages() {
        return this.supportedLanguages.values();
    }

    public void registerReloadListener(TranslationsReloadListener reloadListener) {
        this.reloadListeners.add(reloadListener);
    }

    private int getTranslationKeyCount() {
        return this.translations.get(this.systemLanguage.definition().code()).size();
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        CompletableFuture<Multimap<String, Supplier<TranslationMap>>> future = CompletableFuture.supplyAsync(() -> {
            this.reload();
            return LanguageReader.collectTranslationSuppliers(manager);
        });

        return future.thenCompose(synchronizer::whenPrepared)
                .thenAcceptAsync(v -> {
                    Multimap<String, Supplier<TranslationMap>> languageSuppliers = future.join();
                    languageSuppliers.forEach(this.translations::add);

                    int keyCount = ServerTranslations.INSTANCE.getTranslationKeyCount();
                    LOGGER.info(new TranslatableText("text.translated_server.loaded.translation_key", String.valueOf(keyCount)).getString());
                });
    }

    @Override
    public String getName() {
        return ResourceReloader.super.getName();
    }
}
