package xyz.nucleoid.server.translations.impl;

import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;

public interface LocalizableComponent {

    static Component asLocalizedFor(final Component text, final ServerLanguage language) {
        // Encode to *any* intermediary format to apply the translation steps from TranslatableTextContentMixin to all translation text codecs
        var dynamicOps = NbtOps.INSTANCE;
        var optional = ScopedValue.where(ServerTranslations.TRANSLATION_CONTEXT, language).call(() -> {
            return ComponentSerialization.CODEC.encodeStart(dynamicOps, text).result();
        });
        if (optional.isEmpty()) {
            // Failed to encode text, shouldn't happen
            return text;
        }

        // Decode back
        var optionalText = ComponentSerialization.CODEC.parse(dynamicOps, optional.get()).result();
        // Failed to decode text, shouldn't happen
        return optionalText.orElse(text);
    }

}
