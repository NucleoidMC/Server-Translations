package fr.catcore.server.translations;

import fr.catcore.server.translations.api.resource.language.ServerLanguageDefinition;
import fr.catcore.server.translations.api.resource.language.ServerLanguageManager;
import fr.catcore.server.translations.config.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.SERVER)
public class ServerTranslations implements ModInitializer {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing ServerTranslations.");
        TranslationGatherer.init();
        LOGGER.info("Initialized ServerTranslations.");

        String systemCode = ConfigManager.getLanguageCodeFromConfig();
        ServerLanguageDefinition language = ServerLanguageManager.INSTANCE.getLanguage(systemCode).getDefinition();
        ServerLanguageManager.INSTANCE.setSystemLanguage(language);
        LOGGER.info(new TranslatableText("text.translated_server.language.set", language.getCode(), language.getName(), language.getRegion()).getString());
        ServerLanguageManager.INSTANCE.registerReloadStartListener(TranslationGatherer::init);
    }
}
