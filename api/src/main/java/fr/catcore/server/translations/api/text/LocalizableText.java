package fr.catcore.server.translations.api.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public interface LocalizableText extends Text {
    static Text asLocalizedFor(Text text, LocalizationTarget target) {
        if (text instanceof LocalizableText) {
            return ((LocalizableText) text).asLocalizedFor(target);
        }
        return text;
    }

    static boolean shouldLocalize(Text text) {
        if (text instanceof LocalizableText) {
            return ((LocalizableText) text).shouldLocalize();
        }
        return false;
    }

    default Text asLocalizedFor(LocalizationTarget target) {
        if (!this.shouldLocalize()) {
            return this;
        }

        LocalizedTextBuilder builder = new LocalizedTextBuilder();
        this.visitLocalized(builder, target, this.getStyle());

        return builder.getResult();
    }

    default boolean shouldLocalize() {
        return this.shouldLocalizeSelf() || !this.getSiblings().isEmpty();
    }

    default boolean shouldLocalizeSelf() {
        return this instanceof TranslatableText;
    }

    void visitLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style);

    void visitSelfLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style);
}
