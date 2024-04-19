package xyz.nucleoid.server.translations.impl;

import com.mojang.serialization.JavaOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.*;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;

import java.util.Optional;


public interface LocalizableText {

    static Text asLocalizedFor(final Text text, final ServerLanguage language) {
        // Encode to *any* intermediary format to apply the translation steps from TranslatableTextContentMixin to all translation text codecs
        var dynamicOps = NbtOps.INSTANCE;
        ServerTranslations.TRANSLATION_CONTEXT.set(language);
        var optional = TextCodecs.CODEC.encodeStart(dynamicOps, text).result();
        ServerTranslations.TRANSLATION_CONTEXT.remove();
        if (optional.isEmpty()) {
            // Failed to encode text, shouldn't happen
            return text;
        }

        // Decode back
        var optionalText = TextCodecs.CODEC.parse(dynamicOps, optional.get()).result();
        // Failed to decode text, shouldn't happen
        return optionalText.orElse(text);
    }

}
