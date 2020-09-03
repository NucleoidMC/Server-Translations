package fr.catcore.server.translations.api.mixin;

import fr.catcore.server.translations.api.resource.language.DelegatedLanguage;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Language.class)
public class MixinLanguage {
    @Shadow
    private static Language instance;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void init(CallbackInfo ci) {
        DelegatedLanguage.INSTANCE.setVanilla(instance);
        instance = DelegatedLanguage.INSTANCE;
    }
}
