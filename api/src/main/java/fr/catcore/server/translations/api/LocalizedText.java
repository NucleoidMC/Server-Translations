package fr.catcore.server.translations.api;

import fr.catcore.server.translations.api.resource.language.ServerLanguageManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Language;

public final class LocalizedText extends TranslatableText {
    private final LocalizationTarget target;

    public LocalizedText(ServerPlayerEntity target, String key) {
        this((LocalizationTarget) target, key);
    }

    public LocalizedText(ServerPlayerEntity target, String key, Object... args) {
        this((LocalizationTarget) target, key, args);
    }

    public LocalizedText(LocalizationTarget target, String key) {
        super(key);
        this.target = target;
    }

    public LocalizedText(LocalizationTarget target, String key, Object... args) {
        super(key, args);
        this.target = target;
    }

    @Override
    protected void updateTranslations() {
        Language language = this.getTargetLanguage();
        if (language != this.languageCache) {
            this.languageCache = language;
            this.translations.clear();

            String translation = language.get(this.key);
            try {
                this.setTranslation(translation);
            } catch (TranslationException e) {
                this.translations.clear();
                this.translations.add(StringVisitable.plain(translation));
            }
        }
    }

    private Language getTargetLanguage() {
        String code = this.target.getLanguageCode();
        return ServerLanguageManager.INSTANCE.getLanguage(code);
    }

    public Text asLiteral() {
        this.updateTranslations();

        MutableText literal = this.selfAsLiteral();

        for (Text sibling : this.siblings) {
            if (literal == null) {
                literal = sibling.shallowCopy();
            } else {
                literal = literal.append(sibling);
            }
        }

        return literal != null ? literal : LiteralText.EMPTY;
    }

    private MutableText selfAsLiteral() {
        MutableText literal = null;

        for (StringVisitable entry : this.translations) {
            Text text;
            if (entry instanceof Text) {
                text = (Text) entry;
            } else {
                text = new LiteralText(entry.getString());
            }

            if (literal == null) {
                literal = text.shallowCopy();
            } else {
                literal = literal.append(text);
            }
        }

        return literal;
    }
}
