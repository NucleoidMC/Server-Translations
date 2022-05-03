package fr.catcore.server.translations.api.mixin.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.ServerTranslations;
import fr.catcore.server.translations.api.resource.language.TranslationAccess;
import fr.catcore.server.translations.api.text.LocalizableTextContent;
import fr.catcore.server.translations.api.text.LocalizedTextVisitor;
import net.minecraft.text.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(TranslatableTextContent.class)
public abstract class TranslatableTextContentMixin implements TextContent, LocalizableTextContent {

    @Shadow
    @Final
    private static StringVisitable LITERAL_PERCENT_SIGN;

    @Shadow
    @Final
    private Object[] args;

    @Shadow
    protected abstract StringVisitable getArg(int index);

    @Shadow
    @Final
    private static Pattern ARG_FORMAT;

    @Shadow
    @Final
    private String key;

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
            return target.getLanguage().remote();
        } else {
            return ServerTranslations.INSTANCE.getSystemLanguage().local();
        }
    }

    @Override
    public void visitSelfLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Text text, Style style) {
        List<StringVisitable> translations = this.buildTranslations(target);
        if (translations != null) {
            this.visitSelfTranslated(visitor, style, translations);
        } else {
            this.visitSelfUntranslated(visitor, text, style);
        }
    }

    private void visitSelfTranslated(LocalizedTextVisitor visitor, Style style, List<StringVisitable> translations) {
        visitor.acceptLiteral("", style);
        for (StringVisitable translation : translations) {
            if (translation instanceof MutableText mutableText) {
                visitor.accept(mutableText);
            } else {
                translation.visit(visitor.asGeneric(style));
            }
        }
    }

    private void visitSelfUntranslated(LocalizedTextVisitor visitor, Text text, Style style) {
        visitor.accept(text.shallowCopy().setStyle(style));
    }

}
