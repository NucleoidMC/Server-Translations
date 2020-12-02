package fr.catcore.server.translations.api;

import fr.catcore.server.translations.api.text.LocalizedLiteralBuilder;
import fr.catcore.server.translations.api.text.LocalizedTextVisitor;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

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

        LocalizedLiteralBuilder builder = new LocalizedLiteralBuilder();
        this.visitLocalized(builder, target, this.getStyle());

        return builder.getResult();
    }

    default boolean shouldLocalize() {
        return !(this instanceof LiteralText && this.getSiblings().isEmpty());
    }

    void visitLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style);

    void visitSelfLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style);
}
