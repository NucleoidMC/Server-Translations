package fr.catcore.server.translations.mixin;

import fr.catcore.server.translations.ServerTranslations;
import fr.catcore.server.translations.resource.language.ServerLanguage;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Language.class)
public class MixinLanguage {

    @Inject(method = "create", cancellable = true, at = @At("RETURN"))
    private static void server_create(CallbackInfoReturnable<Language> cir) {
        ServerTranslations.onInitialize();
        cir.setReturnValue(ServerLanguage.getInstance());
    }

    @Inject(method = "getInstance", cancellable = true, at = @At("RETURN"))
    private static void server_GetInstance(CallbackInfoReturnable<Language> cir) {
        cir.setReturnValue(ServerLanguage.getInstance());
    }
}
