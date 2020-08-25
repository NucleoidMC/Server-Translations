package fr.catcore.server.translations.mixin;

import fr.catcore.server.translations.ServerPlayerEntityAccessor;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity implements ServerPlayerEntityAccessor {

    private String language;

    @Inject(at = @At("RETURN"), method = "setClientSettings")
    private void setLanguage(ClientSettingsC2SPacket packet, CallbackInfo ci) {
        this.language = ((ClientSettingsC2SPacketAccessor) packet).getLanguage();
    }

    @Override
    public String getLanguage() {
        return this.language;
    }
}
