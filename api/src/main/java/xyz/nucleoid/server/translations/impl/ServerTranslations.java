package xyz.nucleoid.server.translations.impl;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.api.language.ServerLanguageDefinition;
import xyz.nucleoid.server.translations.impl.language.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class ServerTranslations implements PreparableReloadListener, ModInitializer, ServerLifecycleEvents.ServerStopped {
    public static final String ID = "server_translations_api";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final PacketContext.Key<String> LANGUAGE_KEY = PacketContext.key(id("lang"));
    public static final ModContainer CONTAINER = FabricLoader.getInstance().getModContainer(ID).orElseThrow();

    public static final ServerTranslations INSTANCE = new ServerTranslations();
    public static final ScopedValue<ServerLanguage> TRANSLATION_CONTEXT = ScopedValue.newInstance();

    private final SortedMap<String, ServerLanguageDefinition> supportedLanguages = new Object2ObjectRBTreeMap<>();

    private final TranslationStore translations = new TranslationStore();
    private final Map<String, ServerLanguage> serverLanguages = new Object2ObjectOpenHashMap<>();
    private final List<TranslationsReloadListener> reloadListeners = new ArrayList<>();
    private final Map<String, String> CODE_ALIAS = new HashMap<>();
    private ServerLanguage defaultLanguage;
    @Nullable
    private ServerLanguage systemLanguage;
    public final LocalizationTarget systemTarget = () -> this.getSystemLanguage().definition().code();

    private ServerTranslations() {
        this.loadSupportedLanguages();
        this.reload();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ID, path);
    }

    @Nullable
    public static ServerLanguage getTranslationContextOrNull() {
        if (TRANSLATION_CONTEXT.isBound()) {
            return TRANSLATION_CONTEXT.get();
        }

        var packet = LocalizationTarget.forPacket();
        return packet != null ? packet.getLanguage() : null;
    }

    private void loadSupportedLanguages() {
        try {
            Pair<List<ServerLanguageDefinition>, Map<String, String>> pair = ServerLanguageDefinitionReaders.loadLanguageDefinitions();
            List<ServerLanguageDefinition> definitions = pair.getFirst();
            for (ServerLanguageDefinition language : definitions) {
                this.supportedLanguages.put(language.code(), language);
            }

            CODE_ALIAS.putAll(pair.getSecond());
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

    public void addTranslations(String code, Supplier<@Nullable TranslationMap> supplier) {
        this.translations.add(code, supplier);
    }

    public ServerLanguage getLanguage(@Nullable LocalizationTarget target) {
        return this.getLanguage(target != null ? target.getLanguageCode() : null);
    }

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

    public ServerLanguage getSystemLanguage() {
        return Objects.requireNonNull(this.systemLanguage);
    }

    public void setSystemLanguage(ServerLanguageDefinition definition) {
        this.systemLanguage = Objects.requireNonNull(this.getLanguage(definition.code()));
    }

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
        return this.translations.get(this.getSystemLanguage().definition().code()).size();
    }

    @Override
    public CompletableFuture<Void> reload(SharedState store, Executor taskExecutor, PreparationBarrier preparationBarrier, Executor reloadExecutor) {
        CompletableFuture<Multimap<String, Supplier<TranslationMap>>> future = CompletableFuture.supplyAsync(() -> {
            this.reload();
            return LanguageReader.collectDataPackTranslations(store.resourceManager());
        });

        return future.thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(v -> {
                    Multimap<String, Supplier<TranslationMap>> languageSuppliers = future.join();
                    languageSuppliers.forEach(this.translations::add);

                    int keyCount = ServerTranslations.INSTANCE.getTranslationKeyCount();
                    LOGGER.info(Component.translatable("text.translated_server.loaded.translation_key", String.valueOf(keyCount)).getString());

                    //System.out.println(LocalizableText.asLocalizedFor(
                    //        Text.translatable("text.translated_server.loaded.translation_key"), ServerLanguage.getLanguage("en_us")).getContent() instanceof TranslatableTextContent t ? t.getFallback() : "[FAIL]");
                });
    }

    public String getCodeAlias(String code) {
        return CODE_ALIAS.getOrDefault(code, code);
    }

    @Override
    public void onInitialize() {
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(id("translations"), ServerTranslations.INSTANCE);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerTranslations.INSTANCE);
    }

    @Override
    public void onServerStopped(MinecraftServer server) {
        this.translations.clear();
        this.serverLanguages.clear();
    }
}
