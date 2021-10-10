package fr.catcore.server.translations.api.resource.language;

import fr.catcore.server.translations.api.ServerTranslations;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Language;

public final class SystemDelegatedLanguage extends Language {
    public static final SystemDelegatedLanguage INSTANCE = new SystemDelegatedLanguage();

    private Language vanilla = Language.getInstance();

    private SystemDelegatedLanguage() {
    }

    public void setVanilla(Language language) {
        this.vanilla = language;
    }

    public Language getVanilla() {
        return this.vanilla;
    }

    @Override
    public String get(String key) {
        String override = this.getSystemLanguage().local().getOrNull(key);
        if (override != null) {
            return override;
        }
        return this.vanilla.get(key);
    }

    @Override
    public boolean hasTranslation(String key) {
        return this.vanilla.hasTranslation(key) || this.getSystemLanguage().local().contains(key);
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
