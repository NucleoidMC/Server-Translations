package xyz.nucleoid.server.translations.impl.language;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.ServerTranslations;

public final class SystemDelegatedLanguage extends Language {
    private final Language vanilla;

    public SystemDelegatedLanguage(Language vanilla) {
        this.vanilla = vanilla;
    }

    @Override
    public String getOrDefault(String elementId) {
        String override = this.getSystemLanguage().serverTranslations().getOrNull(elementId);
        if (override != null) {
            return this.vanilla.getOrDefault(elementId, override);
        }
        return this.vanilla.getOrDefault(elementId);
    }

    @Override
    public String getOrDefault(String elementId, String defaultValue) {
        return this.vanilla.getOrDefault(elementId, defaultValue);
    }

    @Override
    public boolean has(String key) {
        return this.vanilla.has(key) || this.getSystemLanguage().serverTranslations().contains(key);
    }

    @Override
    public boolean isDefaultRightToLeft() {
        return this.getSystemLanguage().definition().rightToLeft();
    }

    private ServerLanguage getSystemLanguage() {
        return ServerTranslations.INSTANCE.getSystemLanguage();
    }

    @Override
    public FormattedCharSequence getVisualOrder(FormattedText text) {
        return this.vanilla.getVisualOrder(text);
    }
}
