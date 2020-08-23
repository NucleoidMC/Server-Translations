package fr.catcore.server.translations;

import fr.catcore.server.translations.config.ConfigManager;
import fr.catcore.server.translations.resource.language.ServerLanguageDefinition;
import fr.catcore.server.translations.resource.language.ServerLanguageManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

@Environment(EnvType.SERVER)
public class ServerTranslations {

    private static final Logger LOGGER = LogManager.getLogger();

    private static boolean loaded = false;

    public static void onInitialize() {
        if (loaded) return;
        loaded = true;
        LOGGER.info("Initializing ServerTranslations.");
        TranslationGatherer.init();
        LOGGER.info("Initialized ServerTranslations.");
        TranslationGatherer.setLanguage(ConfigManager.getLanguageCodeFromConfig());
        ServerLanguageDefinition languageDefinition = ServerLanguageManager.getInstance().getLanguage();
        LOGGER.info("Language set to {}: {} ({})", languageDefinition.getCode(), languageDefinition.getName(), languageDefinition.getRegion());
    }

    public static void sendTranslatedMessageToAll(TranslatableText translatableText, MessageType messageType, UUID uuid, MinecraftServer minecraftServer) {
        PlayerManager playerManager = minecraftServer.getPlayerManager();
        for (ServerPlayerEntity playerEntity : playerManager.getPlayerList()) {
            sendTranslatedMessage(translatableText, messageType, uuid, playerEntity);
        }
    }

    public static void sendTranslatedMessage(TranslatableText translatableText, MessageType messageType, UUID uuid, PlayerEntity playerEntity) {
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) playerEntity;
        String language = ((ServerPlayerEntityAccessor) serverPlayerEntity).getLanguage();
        String message = TranslationGatherer.getTranslation(language, translatableText);
        serverPlayerEntity.sendMessage(new LiteralText(message), messageType, uuid);
    }
}
