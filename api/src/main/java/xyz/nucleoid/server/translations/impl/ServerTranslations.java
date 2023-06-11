package xyz.nucleoid.server.translations.impl;

import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.api.language.ServerLanguageDefinition;
import xyz.nucleoid.server.translations.impl.language.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class ServerTranslations implements IdentifiableResourceReloadListener, ModInitializer {
    public static final String ID = "server_translations_api";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ServerTranslations INSTANCE = new ServerTranslations();

    private final SortedMap<String, ServerLanguageDefinition> supportedLanguages = new Object2ObjectRBTreeMap<>();

    private final TranslationStore translations = new TranslationStore();
    private final Map<String, ServerLanguage> serverLanguages = new Object2ObjectOpenHashMap<>();
    public final LocalizationTarget systemTarget = () -> this.getSystemLanguage().definition().code();

    private ServerLanguage defaultLanguage;
    private ServerLanguage systemLanguage;

    private final List<TranslationsReloadListener> reloadListeners = new ArrayList<>();

    private final Map<String, String> CODE_ALIAS = new HashMap<>();

    private ServerTranslations() {
        this.loadSupportedLanguages();
        this.reload();
    }

    private void loadSupportedLanguages() {
        try {
            Pair<List<ServerLanguageDefinition>, Map<String, String>> pair = ServerLanguageDefinitionReaders.loadLanguageDefinitions();
            List<ServerLanguageDefinition> definitions = pair.getLeft();
            for (ServerLanguageDefinition language : definitions) {
                this.supportedLanguages.put(language.code(), language);
            }

            CODE_ALIAS.putAll(pair.getRight());
        } catch (IOException e) {
            LOGGER.error("Failed to load server language definitions", e);
        }
    }

    private void reload() {
        this.translations.clear();
        this.serverLanguages.clear();

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

        ServerLanguage language = this.serverLanguages.get(code);
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

        ServerLanguage language = new ServerLanguage(definition, translations.union(defaultTranslations));
        this.serverLanguages.put(definition.code(), language);

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
            return LanguageReader.collectDataPackTranslations(manager);
        });

        return future.thenCompose(synchronizer::whenPrepared)
                .thenAcceptAsync(v -> {
                    Multimap<String, Supplier<TranslationMap>> languageSuppliers = future.join();
                    languageSuppliers.forEach(this.translations::add);

                    int keyCount = ServerTranslations.INSTANCE.getTranslationKeyCount();
                    LOGGER.info(Text.translatable("text.translated_server.loaded.translation_key", String.valueOf(keyCount)).getString());
                });
    }

    @Override
    public String getName() {
        return IdentifiableResourceReloadListener.super.getName();
    }

    public String getCodeAlias(String code) {
        return CODE_ALIAS.getOrDefault(code, code);
    }

    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(ServerTranslations.INSTANCE);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(ID, "translations");
    }
}
