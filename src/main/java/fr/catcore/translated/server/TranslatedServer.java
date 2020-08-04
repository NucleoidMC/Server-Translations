package fr.catcore.translated.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
public class TranslatedServer {

    public static void onInitialize() {
        TranslationGatherer.init();
        TranslationGatherer.setLanguage("fr_fr");
    }
}
