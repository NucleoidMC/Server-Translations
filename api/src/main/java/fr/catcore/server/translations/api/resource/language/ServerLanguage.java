package fr.catcore.server.translations.api.resource.language;

public record ServerLanguage(ServerLanguageDefinition definition,
                             TranslationAccess local,
                             TranslationAccess remote) {
}
