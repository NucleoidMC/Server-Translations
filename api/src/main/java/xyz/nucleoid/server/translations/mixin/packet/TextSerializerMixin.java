package xyz.nucleoid.server.translations.mixin.packet;

import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.impl.LocalizableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Text.Serializer.class)
public abstract class TextSerializerMixin {
    @ModifyVariable(method = "serialize(Lnet/minecraft/text/Text;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;", at = @At("HEAD"), ordinal = 0)
    private Text stapi$serializeTranslatableText(Text text) {
        LocalizationTarget target = LocalizationTarget.forPacket();
        if (target != null) {
            return LocalizableText.asLocalizedFor(text, target.getLanguage(), false);
        }
        return text;
    }
}
