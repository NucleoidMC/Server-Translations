package xyz.nucleoid.server.translations.api.language;


import xyz.nucleoid.server.translations.impl.ServerTranslations;

public record ServerLanguageDefinition(String code, String region, String name,
                                       boolean rightToLeft) implements Comparable<ServerLanguageDefinition> {
    public static final String DEFAULT_CODE = "en_us";
    public static final ServerLanguageDefinition DEFAULT = new ServerLanguageDefinition(DEFAULT_CODE, "US", "English", false);

    public static Iterable<ServerLanguageDefinition> getAllLanguages() {
        return ServerTranslations.INSTANCE.getAllLanguages();
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", this.region, this.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            return o instanceof ServerLanguageDefinition languageDefinition && this.code.equals(languageDefinition.code);
        }
    }

    @Override
    public int hashCode() {
        return this.code.hashCode();
    }

    @Override
    public int compareTo(ServerLanguageDefinition languageDefinition) {
        return this.code.compareTo(languageDefinition.code);
    }
}
