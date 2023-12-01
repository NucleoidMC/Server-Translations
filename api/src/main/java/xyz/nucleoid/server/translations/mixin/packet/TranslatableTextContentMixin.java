package xyz.nucleoid.server.translations.mixin.packet;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.server.translations.api.LocalizationTarget;

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
        return original.xmap(Function.identity(), (content) -> {
            if (content.getFallback() == null) {
                var target = LocalizationTarget.forPacket();
                if (target != null) {
                    return new TranslatableTextContent(content.getKey(), target.getLanguage().serverTranslations().get(content.getKey()), content.getArgs());
                }
            }
            return content;
        });
    }

}
