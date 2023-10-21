package xyz.nucleoid.server.translations.mixin.packet;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.impl.LocalizableText;

import java.util.List;
import java.util.function.Function;

@Mixin(TextCodecs.class)
public class TextCodecsMixin {

    // Alternative (incompatible with message-api)
    /*@Inject(method = "createCodec", at = @At("RETURN"), cancellable = true)
    private static void modifyCodec(Codec<Text> selfCodec, CallbackInfoReturnable<Codec<Text>> cir) {
        cir.setReturnValue(
            cir.getReturnValue().xmap(Function.identity(),
                text -> {
                    LocalizationTarget target = LocalizationTarget.forPacket();
                    if (target != null) {
                        return LocalizableText.asLocalizedFor(text, target.getLanguage(), false);
                    }
                    return text;
                })
        );
    }*/

    @Redirect(
        method = "createCodec",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/serialization/Codec;xmap(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"
        )
    )
    private static Codec<Text> modifyCodec(Codec<Either<Either<String, List<Text>>, Text>> instance, Function<? super Either<Either<String, List<Text>>, Text>, Text> to, Function<? super Text, Either<Either<String, List<Text>>, Text>> from) {
        return instance.xmap(to, from).xmap(Function.identity(),
            text -> {
                LocalizationTarget target = LocalizationTarget.forPacket();
                if (target != null) {
                    return LocalizableText.asLocalizedFor(text, target.getLanguage(), false);
                }
                return text;
            });
    }

}
