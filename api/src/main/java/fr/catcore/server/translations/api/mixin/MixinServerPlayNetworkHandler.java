package fr.catcore.server.translations.api.mixin;

import fr.catcore.server.translations.api.LocalizableText;
import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
    @Shadow
    public ServerPlayerEntity player;

    @ModifyArg(
            method = "sendPacket(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V"
            )
    )
    private Packet<?> modifyPacket(Packet<?> packet) {
        if (packet instanceof GameMessageS2CPacket) {
            GameMessageS2CPacketAccessor accessor = (GameMessageS2CPacketAccessor) packet;

            Text message = accessor.getMessage();
            Text localized = LocalizableText.asLocalizedFor(message, (LocalizationTarget) this.player);
            if (localized != message) {
                return new GameMessageS2CPacket(localized, accessor.getLocation(), accessor.getSenderUuid());
            }
        }

        return packet;
    }
}

