package fr.catcore.server.translations.api.mixin.packet;

import fr.catcore.server.translations.api.text.LocalizableText;
import net.minecraft.network.packet.s2c.play.ChatPreviewS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChatPreviewS2CPacket.class)
public class ChatPreviewS2CPacketMixin {
    @ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeNullable(Ljava/lang/Object;Lnet/minecraft/network/PacketByteBuf$PacketWriter;)V"))
    private <T> T stapi$dontLocalize(T text) {
        if (text != null) {
            var copy = ((Text) text).copy();
            ((LocalizableText) copy).setLocalized(true);
            return (T) copy;
        }
        return text;
    }
}
