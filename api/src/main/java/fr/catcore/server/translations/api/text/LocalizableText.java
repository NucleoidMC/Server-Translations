package fr.catcore.server.translations.api.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public interface LocalizableText extends Text {

    static Text asLocalizedFor(Text text, LocalizationTarget target) {
        if (text instanceof LocalizableText localizableText) {
            return localizableText.asLocalizedFor(target);
        }
        return text;
    }

    default Text asLocalizedFor(LocalizationTarget target) {
        LocalizedTextBuilder builder = new LocalizedTextBuilder();
        this.visitText(builder, target, this.getStyle());

        return builder.getResult();
    }

    void visitText(LocalizedTextVisitor visitor, LocalizationTarget target, Style style);

}
