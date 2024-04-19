package xyz.nucleoid.server.translations.mixin.codec;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.MapCodec;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.ServerTranslations;

import java.util.function.Function;

@Mixin(TranslatableTextContent.class)
public abstract class TranslatableTextContentMixin {

    @ModifyExpressionValue(
        method = "<clinit>",
        at = @At(value = "INVOKE",
            target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;mapCodec(Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;"
        )
    )
    private static MapCodec<TranslatableTextContent> stapi$addTranslationFallback(MapCodec<TranslatableTextContent> original) {
        return original.xmap((content) -> {
            if (content.getFallback() != null) {
                ServerLanguage language = ServerTranslations.TRANSLATION_CONTEXT.get();
                if (language == null) {
                    var target = LocalizationTarget.forPacket();
                    if (target != null) {
                        language = target.getLanguage();
                    }
                }

                if (language != null && content.getFallback().equals(language.serverTranslations().getOrNull(content.getKey()))) {
                    return new TranslatableTextContent(content.getKey(), null, content.getArgs());
                }
            }
            return content;
        }, (content) -> {
            if (content.getFallback() == null) {
                ServerLanguage language = ServerTranslations.TRANSLATION_CONTEXT.get();
                if (language == null) {
                    var target = LocalizationTarget.forPacket();
                    if (target != null) {
                        language = target.getLanguage();
                    }
                }
                if (language != null) {
                    return new TranslatableTextContent(content.getKey(), language.serverTranslations().get(content.getKey()), content.getArgs());
                }
            }
            return content;
        });
    }
}
