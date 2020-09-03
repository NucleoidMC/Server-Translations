package fr.catcore.server.translations.api.resource.language;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Language;

import java.util.Optional;

public final class DelegatedLanguage extends Language {
    public static final DelegatedLanguage INSTANCE = new DelegatedLanguage();

    private Language vanilla = Language.getInstance();

    private DelegatedLanguage() {
    }

    public void setVanilla(Language language) {
        this.vanilla = language;
    }

    @Override
    public String get(String key) {
        String override = ServerLanguageManager.INSTANCE.getSystemLanguage().get(key);
        if (override != key) {
            return override;
        }
        return this.vanilla.get(key);
    }

    @Override
    public boolean hasTranslation(String key) {
        return this.vanilla.hasTranslation(key) || ServerLanguageManager.INSTANCE.getSystemLanguage().hasTranslation(key);
    }

    @Override
    public boolean isRightToLeft() {
        return ServerLanguageManager.INSTANCE.getSystemLanguage().isRightToLeft();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public OrderedText reorder(StringVisitable text) {
        return visitor -> text.visit((style, string) -> {
            return TextVisitFactory.visitFormatted(string, style, visitor) ? Optional.empty() : StringVisitable.TERMINATE_VISIT;
        }, Style.EMPTY).isPresent();
    }
}
