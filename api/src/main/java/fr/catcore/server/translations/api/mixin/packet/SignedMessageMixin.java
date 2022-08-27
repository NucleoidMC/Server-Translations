package fr.catcore.server.translations.api.mixin.packet;

import fr.catcore.server.translations.api.text.LocalizableText;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

@Mixin(SignedMessage.class)
public class SignedMessageMixin {
    @ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeOptional(Ljava/util/Optional;Lnet/minecraft/network/PacketByteBuf$PacketWriter;)V"))
    private <T> Optional<T> stapi$dontLocalize(Optional<T> text) {
        if (text.isPresent()) {
            var copy = ((Text) text.get()).copy();
            ((LocalizableText) copy).setLocalized(true);
            return Optional.of((T) copy);
        }
        return text;
    }
}
