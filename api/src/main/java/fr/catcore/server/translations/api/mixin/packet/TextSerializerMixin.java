package fr.catcore.server.translations.api.mixin.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Mixin(Text.Serializer.class)
public abstract class TextSerializerMixin {
    @Shadow
    public abstract JsonElement serialize(Text text, Type type, JsonSerializationContext ctx);

    @Inject(method = "serialize(Lnet/minecraft/text/Text;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;", at = @At("HEAD"), cancellable = true)
    private void serializeTranslatableText(Text text, Type type, JsonSerializationContext ctx, CallbackInfoReturnable<JsonElement> ci) {
        LocalizationTarget target = LocalizationTarget.forPacket();
        if (target != null) {
            Text localized = LocalizableText.asLocalizedFor(text, target);
            if (!text.equals(localized)) {
                ci.setReturnValue(this.serialize(localized, localized.getClass(), ctx));
            }
        }
    }
}
