package xyz.nucleoid.server.translations.api.language;

import xyz.nucleoid.server.translations.impl.ServerTranslations;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public record ServerLanguage(ServerLanguageDefinition definition,
                             TranslationAccess serverTranslations) {


    public static ServerLanguage getLanguage(@Nullable String code) {
        return ServerTranslations.INSTANCE.getLanguage(code);
    }

    public static ServerLanguage getLanguage(ServerLanguageDefinition languageDefinition) {
        return ServerTranslations.INSTANCE.getLanguage(languageDefinition.code());
    }
}
