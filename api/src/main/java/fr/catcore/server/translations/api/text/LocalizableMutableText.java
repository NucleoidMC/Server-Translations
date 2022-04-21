package fr.catcore.server.translations.api.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public interface LocalizableMutableText extends Text {

    static Text asLocalizedFor(Text text, LocalizationTarget target) {
        if (text instanceof LocalizableMutableText localizableText) {
            return localizableText.asLocalizedFor(target);
        }
        // Should never happen
        return text;
    }

    default Text asLocalizedFor(LocalizationTarget target) {
        if (!this.shouldLocalize()) {
            return this;
        }

        LocalizedTextBuilder builder = new LocalizedTextBuilder();
        this.visitLocalizedText(builder, target, this.getStyle());

        return builder.getResult();
    }

    static boolean shouldLocalize(Text text) {
        if (text instanceof LocalizableMutableText localizableText) {
            return localizableText.shouldLocalize();
        }
        return false;
    }

    default boolean shouldLocalize() {
        if (this.shouldLocalizeSelf()) {
            return true;
        }

        for (Text sibling : this.getSiblings()) {
            if (LocalizableMutableText.shouldLocalize(sibling)) {
                return true;
            }
        }

        return false;
    }

    default boolean shouldLocalizeSelf() {
        return method_10851() instanceof LocalizableText;
    }

    void visitLocalizedText(LocalizedTextVisitor visitor, LocalizationTarget target, Style style);

}
