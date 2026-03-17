package xyz.nucleoid.server.translations.mixin.codec;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.ServerTranslations;

@Mixin(TranslatableContents.class)
public abstract class TranslatableContentsMixin {

    @ModifyExpressionValue(
        method = "<clinit>",
        at = @At(value = "INVOKE",
            target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;mapCodec(Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;"
        )
    )
    private static MapCodec<TranslatableContents> stapi$addTranslationFallback(MapCodec<TranslatableContents> original) {
        return original.xmap((content) -> {
            if (content.getFallback() != null) {
                ServerLanguage language = ServerTranslations.getTranslationContextOrNull();
                if (language == null) {
                    var target = LocalizationTarget.forPacket();
                    if (target != null) {
                        language = target.getLanguage();
                    }
                }

                if (language != null && content.getFallback().equals(language.serverTranslations().getOrNull(content.getKey()))) {
                    return new TranslatableContents(content.getKey(), null, content.getArgs());
                }
            }
            return content;
        }, (content) -> {
            if (content.getFallback() == null) {
                ServerLanguage language = ServerTranslations.getTranslationContextOrNull();
                if (language == null) {
                    var target = LocalizationTarget.forPacket();
                    if (target != null) {
                        language = target.getLanguage();
                    }
                }
                if (language != null) {
                    return new TranslatableContents(content.getKey(), language.serverTranslations().get(content.getKey()), content.getArgs());
                }
            }
            return content;
        });
    }
}
