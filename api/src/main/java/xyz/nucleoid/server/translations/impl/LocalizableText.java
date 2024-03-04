package xyz.nucleoid.server.translations.impl;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.*;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;

import java.util.Optional;


public interface LocalizableText {

    static Text asLocalizedFor(final Text text, final ServerLanguage language) {
        // TODO 1.20.5 Not tested
        // Encode to *any* intermediary format to apply the translation steps from TranslatableTextContentMixin to all translation text codecs
        var dynamicOps = NbtOps.INSTANCE;
        Optional<NbtElement> optionalNbt = TextCodecs.CODEC.encodeStart(dynamicOps, text).result();
        if (optionalNbt.isEmpty()) {
            // Failed to encode text, shouldn't happen
            return text;
        }
        // Decode back
        ServerTranslations.TRANSLATION_CONTEXT.set(language);
        Optional<Text> optionalText = TextCodecs.CODEC.parse(dynamicOps, optionalNbt.get()).result();
        ServerTranslations.TRANSLATION_CONTEXT.remove();
        // Failed to decode text, shouldn't happen
        return optionalText.orElse(text);
    }

}
