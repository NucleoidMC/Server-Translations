package xyz.nucleoid.server.translations.mixin;

import xyz.nucleoid.server.translations.impl.language.SystemDelegatedLanguage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Language.class)
public class LanguageMixin {
    @Shadow
    private static Language instance;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void stapi$init(CallbackInfo ci) {
        instance = new SystemDelegatedLanguage(instance);
    }

    @Environment(EnvType.SERVER)
    @ModifyVariable(method = "setInstance", at = @At("HEAD"))
    private static Language stapi$modifyLanguage(Language language) {
        return new SystemDelegatedLanguage(language);
    }
}
