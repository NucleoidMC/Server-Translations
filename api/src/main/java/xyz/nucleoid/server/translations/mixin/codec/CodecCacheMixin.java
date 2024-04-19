package xyz.nucleoid.server.translations.mixin.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.impl.ServerTranslations;

@Mixin(targets = "net/minecraft/util/dynamic/CodecCache$2")
public class CodecCacheMixin {
    @Shadow
    @Final
    private Codec<Object> field_51505;
    @Inject(method = "encode", at = @At("HEAD"), cancellable = true)
    private void dontCacheOnNetworking(Object value, DynamicOps<Object> ops, Object prefix, CallbackInfoReturnable<Object> cir) {
        if (
                (value instanceof Text || value instanceof WrittenBookContentComponent || value instanceof LoreComponent)
                && (LocalizationTarget.forPacket() != null || ServerTranslations.TRANSLATION_CONTEXT.get() != null)
        ) {
            cir.setReturnValue(this.field_51505.encode(value, ops, prefix));
        }
    }
}
