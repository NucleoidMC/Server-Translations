package xyz.nucleoid.server.translations.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.nucleoid.server.translations.impl.LanguageGetter;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements LanguageGetter {
    @Shadow private String language;

    @Override
    public String stapi$getLanguage() {
        return this.language;
    }
}
