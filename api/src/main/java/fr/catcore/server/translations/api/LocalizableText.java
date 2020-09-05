package fr.catcore.server.translations.api;

import fr.catcore.server.translations.api.resource.language.ServerLanguageManager;
import fr.catcore.server.translations.api.text.LocalizedLiteralBuilder;
import fr.catcore.server.translations.api.text.LocalizedTextVisitor;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

public interface LocalizableText extends Text {
    static Text asLocalizedFor(Text text, LocalizationTarget target) {
        if (text instanceof LocalizableText) {
            return ((LocalizableText) text).asLocalizedFor(target);
        }
        return text;
    }

    static <T extends Text> T withTarget(T text, LocalizationTarget target) {
        setTarget(text, target);
        return text;
    }

    static void setTarget(Text text, LocalizationTarget target) {
        if (text instanceof LocalizableText) {
            ((LocalizableText) text).setTarget(target);
        }
    }

    default Text asLocalizedFor(LocalizationTarget target) {
        LocalizedLiteralBuilder builder = new LocalizedLiteralBuilder();
        this.visitLocalized(builder, target, this.getStyle());

        return builder.getResult();
    }

    default Text asLocalized() {
        LocalizationTarget target = this.getTarget();
        if (target != null) {
            return this.asLocalizedFor(target);
        }
        return this;
    }

    void setTarget(LocalizationTarget target);

    LocalizationTarget getTarget();

    default Language getTargetLanguage() {
        LocalizationTarget target = this.getTarget();
        if (target == null) {
            return ServerLanguageManager.INSTANCE.getSystemLanguage();
        }

        String code = target.getLanguageCode();
        return ServerLanguageManager.INSTANCE.getLanguage(code);
    }

    void visitLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style);

    void visitSelfLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style);
}
