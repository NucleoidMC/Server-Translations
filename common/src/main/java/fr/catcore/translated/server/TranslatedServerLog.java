package fr.catcore.translated.server;

import fr.catcore.translated.server.config.ConfigManager;
import fr.catcore.translated.server.resource.language.ServerLanguageDefinition;
import fr.catcore.translated.server.resource.language.ServerLanguageManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.SERVER)
public class TranslatedServerLog {

    private static final Logger LOGGER = LogManager.getLogger();

    private static boolean loaded = false;

    public static void onInitialize() {
        if (loaded) return;
        loaded = true;
        LOGGER.info("Initializing TranslatedServerLog.");
        TranslationGatherer.init();
        LOGGER.info("Initialized TranslatedServerLog.");
        TranslationGatherer.setLanguage(ConfigManager.getLanguageCodeFromConfig());
        ServerLanguageDefinition languageDefinition = ServerLanguageManager.getInstance().getLanguage();
        LOGGER.info("Language set to {}: {} ({})", languageDefinition.getCode(), languageDefinition.getName(), languageDefinition.getRegion());

    }
}
