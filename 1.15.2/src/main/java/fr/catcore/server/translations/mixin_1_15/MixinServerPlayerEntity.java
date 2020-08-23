package fr.catcore.server.translations.mixin_1_15;

import fr.catcore.server.translations.ServerPlayerEntityAccessor;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity implements ServerPlayerEntityAccessor {

    @Shadow private String clientLanguage;

    @Override
    public String getLanguage() {
        return this.clientLanguage;
    }
}
