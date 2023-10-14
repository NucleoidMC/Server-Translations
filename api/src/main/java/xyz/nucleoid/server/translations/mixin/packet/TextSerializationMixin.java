package xyz.nucleoid.server.translations.mixin.packet;

import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.impl.LocalizableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Text.Serialization.class)
public abstract class TextSerializationMixin {
    @ModifyVariable(method = "toJson", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static Text stapi$serializeTranslatableText(Text text) {
        LocalizationTarget target = LocalizationTarget.forPacket();
        if (target != null) {
            return LocalizableText.asLocalizedFor(text, target.getLanguage(), false);
        }
        return text;
    }
}
