package fr.catcore.server.translations.resource.language;

import net.minecraft.util.Language;

public class ServerLanguage {

    private static volatile Language instance = Language.getInstance();

    public static Language getInstance() {
        return instance;
    }

    public static void setInstance(Language instance) {
        ServerLanguage.instance = instance;
    }
}
