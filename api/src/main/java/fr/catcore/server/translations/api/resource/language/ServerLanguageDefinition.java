package fr.catcore.server.translations.api.resource.language;

import com.google.gson.JsonObject;

import java.util.Locale;

public class ServerLanguageDefinition implements Comparable<ServerLanguageDefinition> {
    private final String code;
    private final String name;
    private final String region;
    private final boolean rightToLeft;

    public ServerLanguageDefinition(String code, String name, String region, boolean rightToLeft) {
        this.code = code;
        this.name = name;
        this.region = region;
        this.rightToLeft = rightToLeft;
    }

    public String getCode() {
        return this.code;
    }

    public static ServerLanguageDefinition parse(String code, JsonObject jsonObject) {
        String region = jsonObject.get("region").getAsString();
        String name = jsonObject.get("name").getAsString();
        boolean bidirectional = jsonObject.get("bidirectional").getAsBoolean();
        return new ServerLanguageDefinition(code.toLowerCase(Locale.ROOT), region, name, bidirectional);
    }

    public String getName() {
        return this.region;
    }

    public String getRegion() {
        return this.name;
    }

    public boolean isRightToLeft() {
        return this.rightToLeft;
    }

    public String toString() {
        return String.format("%s (%s)", this.region, this.name);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            return o instanceof ServerLanguageDefinition && this.code.equals(((ServerLanguageDefinition) o).code);
        }
    }

    public int hashCode() {
        return this.code.hashCode();
    }

    public int compareTo(ServerLanguageDefinition languageDefinition) {
        return this.code.compareTo(languageDefinition.code);
    }
}
