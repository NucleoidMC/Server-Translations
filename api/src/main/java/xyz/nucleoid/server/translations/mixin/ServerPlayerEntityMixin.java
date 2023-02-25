package xyz.nucleoid.server.translations.mixin;

import xyz.nucleoid.server.translations.api.LocalizationTarget;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements LocalizationTarget {
    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Override
    public String getLanguageCode() {
        return this.networkHandler != null ? ((LocalizationTarget) this.networkHandler).getLanguageCode() : null;
    }
}
