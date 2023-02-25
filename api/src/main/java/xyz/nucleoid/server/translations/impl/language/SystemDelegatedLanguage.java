package xyz.nucleoid.server.translations.impl.language;

import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.ServerTranslations;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Language;

public final class SystemDelegatedLanguage extends Language {
    private final Language vanilla;

    public SystemDelegatedLanguage(Language vanilla) {
        this.vanilla = vanilla;
    }

    @Override
    public String get(String key) {
        String override = this.getSystemLanguage().serverTranslations().getOrNull(key);
        if (override != null) {
            return override;
        }
        return this.vanilla.get(key);
    }

    @Override
    public String get(String key, String fallback) {
        String override = this.getSystemLanguage().serverTranslations().getOrNull(key);
        if (override != null) {
            return override;
        }
        return this.vanilla.get(key, fallback);
    }

    @Override
    public boolean hasTranslation(String key) {
        return this.vanilla.hasTranslation(key) || this.getSystemLanguage().serverTranslations().contains(key);
    }

    @Override
    public boolean isRightToLeft() {
        return this.getSystemLanguage().definition().rightToLeft();
    }

    private ServerLanguage getSystemLanguage() {
        return ServerTranslations.INSTANCE.getSystemLanguage();
    }

    @Override
    public OrderedText reorder(StringVisitable text) {
        return this.vanilla.reorder(text);
    }
}
