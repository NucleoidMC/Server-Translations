package fr.catcore.server.translations.api.mixin;

import fr.catcore.server.translations.api.LocalizableText;
import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
    private static final BossBar DUMMY_BOSS_BAR = new ServerBossBar(LiteralText.EMPTY, BossBar.Color.RED, BossBar.Style.PROGRESS);

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
            return this.modifyMessage(packet);
        } else if (packet instanceof BossBarS2CPacket) {
            return this.modifyBossBar(packet);
        } else if (packet instanceof TitleS2CPacket) {
            return this.modifyTitle(packet);
        }

        return packet;
    }

    private Packet<?> modifyMessage(Packet<?> packet) {
        GameMessageS2CPacketAccessor accessor = (GameMessageS2CPacketAccessor) packet;

        Text message = accessor.getMessage();
        Text localized = this.asLocalized(message);
        if (localized != message) {
            return new GameMessageS2CPacket(localized, accessor.getLocation(), accessor.getSenderUuid());
        }

        return packet;
    }

    private Packet<?> modifyBossBar(Packet<?> packet) {
        BossBarS2CPacketAccessor accessor = (BossBarS2CPacketAccessor) packet;

        Text message = accessor.getMessage();
        Text localized = this.asLocalized(message);
        if (localized != message) {
            BossBarS2CPacket newPacket = new BossBarS2CPacket(accessor.getBarType(), DUMMY_BOSS_BAR);
            BossBarS2CPacketAccessor newAccessor = (BossBarS2CPacketAccessor) newPacket;

            newAccessor.setUUID(accessor.getUUID());
            newAccessor.setMessage(localized);
            newAccessor.setBarPercent(accessor.getBarPercent());
            newAccessor.setBarColor(accessor.getBarColor());
            newAccessor.setBarOverlay(accessor.getBarOverlay());
            newAccessor.setBarShouldDarkenSky(accessor.barShouldDarkenSky());
            newAccessor.setBarHasDragonMusic(accessor.barHasDragonMusic());
            newAccessor.setBarShouldThickenFog(accessor.barShouldThickenFog());

            return newPacket;
        }

        return packet;
    }

    private Packet<?> modifyTitle(Packet<?> packet) {
        TitleS2CPacketAccessor accessor = (TitleS2CPacketAccessor) packet;

        Text message = accessor.getMessage();
        Text localized = this.asLocalized(message);
        if (localized != message) {
            return new TitleS2CPacket(
                    accessor.getTitleAction(),
                    localized,
                    accessor.getFadeIn(),
                    accessor.getStay(),
                    accessor.getFadeOut()
            );
        }

        return packet;
    }

    private Text asLocalized(Text message) {
        return LocalizableText.asLocalizedFor(message, (LocalizationTarget) this.player);
    }
}

