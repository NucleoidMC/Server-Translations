package fr.catcore.server.translations;

import com.mojang.logging.LogUtils;
import xyz.nucleoid.server.translations.impl.ServerTranslations;
import xyz.nucleoid.server.translations.api.language.ServerLanguageDefinition;
import fr.catcore.server.translations.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.Text;
import org.slf4j.Logger;

public class ServerTranslationsInitializer implements ModInitializer {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing ServerTranslations.");
        TranslationGatherer.init();
        LOGGER.info("Initialized ServerTranslations.");

        String systemCode = ConfigManager.getLanguageCodeFromConfig();
        ServerLanguageDefinition language = ServerTranslations.INSTANCE.getLanguageDefinition(systemCode);
        ServerTranslations.INSTANCE.setSystemLanguage(language);

        LOGGER.info(Text.translatable("text.translated_server.language.set", language.code(), language.name(), language.region()).getString());

        ServerTranslations.INSTANCE.registerReloadListener(TranslationGatherer::init);
        ServerTranslations.INSTANCE.registerReloadListener(() -> {
            ServerLanguageDefinition lang = ServerTranslations.INSTANCE.getLanguageDefinition(systemCode);
            ServerTranslations.INSTANCE.setSystemLanguage(lang);
        });
    }
}
