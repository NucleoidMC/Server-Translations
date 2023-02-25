package xyz.nucleoid.server.translations.mixin;

import xyz.nucleoid.server.translations.api.LocalizationTarget;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin implements LocalizationTarget {
    @Unique
    private String stapi$language;


    @Inject(method = "onClientSettings", at = @At("TAIL"))
    private void stapi$setLanguage(ClientSettingsC2SPacket packet, CallbackInfo ci) {
        this.stapi$language = packet.language();
    }

    @Override
    public String getLanguageCode() {
        return this.stapi$language;
    }
}
