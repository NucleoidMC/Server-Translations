package fr.catcore.server.translations.api.mixin.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.ServerTranslations;
import fr.catcore.server.translations.api.resource.language.TranslationAccess;
import fr.catcore.server.translations.api.text.LocalizableText;
import fr.catcore.server.translations.api.text.LocalizedTextVisitor;
import net.minecraft.text.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.ArrayList;
import java.util.List;

@Mixin(TranslatableText.class)
public abstract class TranslatableTextMixin implements LocalizableText, MutableText {
    @Shadow
    @Final
    private String key;
    @Shadow
    @Mutable
    @Final
    private List<StringVisitable> translations;

    @Unique
    private final List<StringVisitable> translationsSwap = new ArrayList<>();

    @Shadow
    protected abstract void setTranslation(String translation);

    @Shadow @Final private Object[] args;

    @Nullable
    private List<StringVisitable> buildTranslations(@Nullable LocalizationTarget target) {
        TranslationAccess translations = this.getTranslationsFor(target);
        String translation = translations.getOrNull(this.key);
        if (translation == null) {
            return null;
        }

        List<StringVisitable> result = this.translationsSwap;
        result.clear();

        List<StringVisitable> previousTranslations = this.translations;
        this.translations = result;

        try {
            this.setTranslation(translation);
        } catch (TranslationException e) {
            return null;
        } finally {
            this.translations = previousTranslations;
        }

        return result;
    }

    private TranslationAccess getTranslationsFor(@Nullable LocalizationTarget target) {
        if (target != null) {
            return target.getLanguage().remote;
        } else {
            return ServerTranslations.INSTANCE.getSystemLanguage().local;
        }
    }

    @Override
    public void visitSelfLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style) {
        List<StringVisitable> translations = this.buildTranslations(target);
        if (translations == null) {
            visitor.accept(new TranslatableText(this.key, this.args));
            return;
        }

        for (StringVisitable translation : translations) {
            if (translation instanceof LocalizableText) {
                ((LocalizableText) translation).visitLocalized(visitor, target, style);
            } else {
                translation.visit(visitor.asGeneric(style));
            }
        }
    }
}
