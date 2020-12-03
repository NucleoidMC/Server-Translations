package fr.catcore.server.translations.api.resource.language;

public final class ServerLanguage {
    public final ServerLanguageDefinition definition;
    public final TranslationAccess local;
    public final TranslationAccess remote;

    public ServerLanguage(ServerLanguageDefinition definition, TranslationAccess local, TranslationAccess remote) {
        this.definition = definition;
        this.local = local;
        this.remote = remote;
    }
}
