package fr.catcore.server.translations.api.mixin.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.ServerTranslations;
import fr.catcore.server.translations.api.resource.language.TranslationAccess;
import fr.catcore.server.translations.api.text.LocalizableText;
import fr.catcore.server.translations.api.text.LocalizedTextVisitor;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(TranslatableText.class)
public abstract class TranslatableTextMixin implements LocalizableText, MutableText {
    @Shadow
    @Final
    private static Pattern ARG_FORMAT;
    @Shadow
    @Final
    private static StringVisitable LITERAL_PERCENT_SIGN;

    @Shadow
    @Final
    private String key;
    @Shadow
    @Final
    private Object[] args;

    @Shadow
    protected abstract StringVisitable getArg(int index);

    @Shadow
    public abstract TranslatableText copy();

    @Nullable
    private List<StringVisitable> buildTranslations(@Nullable LocalizationTarget target) {
        TranslationAccess translations = this.getTranslationsFor(target);
        String translation = translations.getOrNull(this.key);
        if (translation == null) {
            return null;
        }

        List<StringVisitable> result = new ArrayList<>();

        // Copy from vanilla TranslatableText#setTranslation to not mutate for thread-safety
        Matcher argumentMatcher = ARG_FORMAT.matcher(translation);

        int currentCharIndex = 0;
        int currentArgumentIndex = 0;

        while (argumentMatcher.find(currentCharIndex)) {
            int argumentStart = argumentMatcher.start();
            int argumentEnd = argumentMatcher.end();

            if (argumentStart > currentCharIndex) {
                String literal = translation.substring(currentCharIndex, argumentStart);
                if (literal.indexOf('%') != -1) {
                    return null;
                }
                result.add(StringVisitable.plain(literal));
            }

            String formatType = argumentMatcher.group(2);
            String literal = translation.substring(argumentStart, argumentEnd);
            if ("%".equals(formatType) && "%%".equals(literal)) {
                result.add(LITERAL_PERCENT_SIGN);
            } else {
                if (!"s".equals(formatType)) {
                    return null;
                }
                String matchedArgumentIndex = argumentMatcher.group(1);
                int argumentIndex = matchedArgumentIndex != null ? Integer.parseInt(matchedArgumentIndex) - 1 : currentArgumentIndex++;
                if (argumentIndex < this.args.length) {
                    result.add(this.getArg(argumentIndex));
                }
            }
            currentCharIndex = argumentEnd;
        }

        if (currentCharIndex < translation.length()) {
            String remaining = translation.substring(currentCharIndex);
            if (remaining.indexOf('%') != -1) {
                return null;
            }
            result.add(StringVisitable.plain(remaining));
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
            visitor.accept(this.copy().setStyle(this.getStyle()));
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
