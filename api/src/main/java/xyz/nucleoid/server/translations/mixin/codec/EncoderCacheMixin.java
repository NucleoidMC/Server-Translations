package xyz.nucleoid.server.translations.mixin.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.WrittenBookContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.impl.ServerTranslations;

@Mixin(targets = "net/minecraft/util/EncoderCache$2")
public class EncoderCacheMixin {
    @Shadow @Final Codec<Object> val$codec;

    @Inject(method = "encode", at = @At("HEAD"), cancellable = true)
    private void dontCacheOnNetworking(Object value, DynamicOps<Object> ops, Object prefix, CallbackInfoReturnable<Object> cir) {
        if (
                (value instanceof Component || value instanceof WrittenBookContent || value instanceof ItemLore)
                && (LocalizationTarget.forPacket() != null || ServerTranslations.TRANSLATION_CONTEXT.isBound())
        ) {
            cir.setReturnValue(this.val$codec.encode(value, ops, prefix));
        }
    }
}
