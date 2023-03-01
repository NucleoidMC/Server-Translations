package xyz.nucleoid.server.translations.impl;

import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.nbt.StackNbtLocalizer;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;


public interface LocalizableText extends Text {
    boolean stapi$isLocalized();

    void stapi$setLocalized(boolean value);

    static Text asLocalizedFor(final Text text, final ServerLanguage language, final boolean translateDeeply) {
        if (isTranslatable(text, translateDeeply)) {
            var content = text.getContent();

            if (content instanceof TranslatableTextContent translatableContent) {
                var args = translatableContent.getArgs();

                if (translateDeeply) {
                    args = new Object[translatableContent.getArgs().length];
                    for (int i = 0; i < args.length; i++) {
                        var arg = translatableContent.getArgs()[i];
                        if (arg instanceof Text argText) {
                            args[i] = asLocalizedFor(argText, language, true);
                        } else {
                            args[i] = arg;
                        }
                    }
                }

                content = new TranslatableTextContent(translatableContent.getKey(),
                        translatableContent.getFallback() == null && language.serverTranslations().contains(translatableContent.getKey())
                                ? language.serverTranslations().get(translatableContent.getKey()) : translatableContent.getFallback(), args
                        );
            }

            var out = MutableText.of(content);
            if (translateDeeply) {
                for (var sibling : text.getSiblings()) {
                    out.append(asLocalizedFor(sibling, language, true));
                }
            } else {
                out.getSiblings().addAll(text.getSiblings());
            }

            var style = text.getStyle();

            if (style.getHoverEvent() != null) {
                if (translateDeeply && style.getHoverEvent().getAction() == HoverEvent.Action.SHOW_TEXT) {
                    style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, asLocalizedFor(style.getHoverEvent().getValue(HoverEvent.Action.SHOW_TEXT), language, true)));
                } else if (style.getHoverEvent().getAction() == HoverEvent.Action.SHOW_ITEM) {
                    var value = style.getHoverEvent().getValue(HoverEvent.Action.SHOW_ITEM);
                    var stack = value.asStack();
                    stack.setNbt(StackNbtLocalizer.localize(stack, stack.getNbt(), language));
                    style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(stack)));
                } else if (translateDeeply && style.getHoverEvent().getAction() == HoverEvent.Action.SHOW_ENTITY) {
                    var value = style.getHoverEvent().getValue(HoverEvent.Action.SHOW_ENTITY);
                    if (value.name != null) {
                        style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(value.entityType, value.uuid, asLocalizedFor(value.name, language, true))));
                    }
                }
            }

            ((LocalizableText) out).stapi$setLocalized(true);
            return out.setStyle(style);
        }

        return text;
    }

    static boolean isTranslatable(Text text, boolean deepLocalization) {
        if (deepLocalization) {
            for (var x : text.getSiblings()) {
                if (isTranslatable(x, true)) {
                    return true;
                }
            }

            if (text.getStyle().getHoverEvent() != null) {
                return true;
            }
        }

        return (text instanceof LocalizableText localizableText && !localizableText.stapi$isLocalized())
                && (text.getContent() instanceof TranslatableTextContent
                || (text.getStyle().getHoverEvent() != null && text.getStyle().getHoverEvent().getAction() == HoverEvent.Action.SHOW_ITEM));
    }
}
