package xyz.nucleoid.server.translations.api.language;

import xyz.nucleoid.server.translations.impl.ServerTranslations;
import org.jetbrains.annotations.Nullable;

public record ServerLanguage(ServerLanguageDefinition definition,
                             TranslationAccess serverTranslations) {


    public static ServerLanguage getLanguage(@Nullable String code) {
        return ServerTranslations.INSTANCE.getLanguage(code);
    }
}
