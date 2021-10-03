package fr.catcore.server.translations;

import fr.catcore.server.translations.api.ServerTranslations;
import fr.catcore.server.translations.api.resource.language.ServerLanguageDefinition;
import fr.catcore.server.translations.config.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.SERVER)
public class ServerTranslationsInitializer implements ModInitializer {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing ServerTranslations.");
        TranslationGatherer.init();
        LOGGER.info("Initialized ServerTranslations.");

        String systemCode = ConfigManager.getLanguageCodeFromConfig();
        ServerLanguageDefinition language = ServerTranslations.INSTANCE.getLanguageDefinition(systemCode);
        ServerTranslations.INSTANCE.setSystemLanguage(language);

        LOGGER.info(new TranslatableText("text.translated_server.language.set", language.code(), language.name(), language.region()).getString());

        ServerTranslations.INSTANCE.registerReloadListener(TranslationGatherer::init);
        ServerTranslations.INSTANCE.registerReloadListener(() -> {
            ServerLanguageDefinition lang = ServerTranslations.INSTANCE.getLanguageDefinition(systemCode);
            ServerTranslations.INSTANCE.setSystemLanguage(lang);
        });
    }
}
