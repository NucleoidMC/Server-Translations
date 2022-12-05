package fr.catcore.server.translations.api.client;

import com.google.common.collect.ImmutableList;
import fr.catcore.server.translations.api.ServerTranslations;
import fr.catcore.server.translations.api.resource.language.ServerLanguageDefinition;
import fr.catcore.server.translations.api.resource.language.SystemDelegatedLanguage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ServerTranslationsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {

            @Override
            public Identifier getFabricId() {
                return new Identifier(ServerTranslations.ID, "system_delegated_language");
            }

            @Override
            public Collection<Identifier> getFabricDependencies() {
                return ImmutableList.of(ResourceReloadListenerKeys.LANGUAGES);
            }

            @Override
            public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
                return synchronizer.whenPrepared(Unit.INSTANCE).thenRunAsync(() -> {
                    String languageCode = MinecraftClient.getInstance().getLanguageManager().getLanguage().getCode();
                    ServerLanguageDefinition languageDefinition = ServerTranslations.INSTANCE.getLanguageDefinition(languageCode);
                    ServerTranslations.INSTANCE.setSystemLanguage(languageDefinition);

                    SystemDelegatedLanguage delegated = SystemDelegatedLanguage.INSTANCE;
                    delegated.setVanilla(Language.getInstance());
                    Language.setInstance(delegated);
                }, applyExecutor);
            }
        });
    }
}
