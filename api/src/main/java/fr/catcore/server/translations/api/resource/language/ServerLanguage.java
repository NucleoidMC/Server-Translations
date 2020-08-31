package fr.catcore.server.translations.api.resource.language;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Language;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ServerLanguage extends Language {
    private final ServerLanguageDefinition definition;
    private final LanguageMap map;

    public ServerLanguage(ServerLanguageDefinition definition, LanguageMap map) {
        this.definition = definition;
        this.map = map;
    }

    public ServerLanguage(ServerLanguageDefinition definition) {
        this(definition, new LanguageMap());
    }

    public ServerLanguageDefinition getDefinition() {
        return this.definition;
    }

    @Override
    public String get(String key) {
        String value = this.map.get(key);

        return value != null ? value : key;
    }

    @Override
    public boolean hasTranslation(String key) {
        return this.map.contains(key);
    }

    @Override
    public boolean isRightToLeft() {
        return this.definition.isRightToLeft();
    }

    public void putAll(LanguageMap map) {
        this.map.putAll(map);
    }

    protected void clearTranslations() {
        this.map.clear();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public OrderedText reorder(StringVisitable text) {
        return visitor -> {
            return text.visit((style, string) -> {
                return TextVisitFactory.visitFormatted(string, style, visitor) ? Optional.empty() : StringVisitable.TERMINATE_VISIT;
            }, Style.EMPTY).isPresent();
        };
    }

    public int getKeyNumber() {
        return this.map.entrySet().size();
    }

    public Set<Map.Entry<String, String>> getEntryList() {
        return this.map.entrySet();
    }
}
