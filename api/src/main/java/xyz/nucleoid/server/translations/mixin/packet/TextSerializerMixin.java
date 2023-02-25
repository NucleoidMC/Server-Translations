package xyz.nucleoid.server.translations.mixin.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.impl.LocalizableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.lang.reflect.Type;

@Mixin(Text.Serializer.class)
public abstract class TextSerializerMixin {
    @Shadow
    public abstract JsonElement serialize(Text text, Type type, JsonSerializationContext ctx);

    @ModifyVariable(method = "serialize(Lnet/minecraft/text/Text;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;", at = @At("HEAD"), ordinal = 0)
    private Text stapi$serializeTranslatableText(Text text) {
        LocalizationTarget target = LocalizationTarget.forPacket();
        if (target != null) {
            return LocalizableText.asLocalizedFor(text, target);
        }
        return text;
    }
}
