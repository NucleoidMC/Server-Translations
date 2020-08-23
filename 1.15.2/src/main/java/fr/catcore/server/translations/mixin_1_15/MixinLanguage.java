package fr.catcore.server.translations.mixin_1_15;

import fr.catcore.server.translations.ServerTranslations;
import fr.catcore.server.translations.resource.language.ServerLanguage;
import fr.catcore.server.translations.resource.language.ServerTranslationStorage;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Language.class)
public class MixinLanguage {

    @Mutable
    @Shadow @Final private static Language INSTANCE;
    private static boolean loaded = false;

    @Inject(method = "<init>", cancellable = true, at = @At("RETURN"))
    private void server_create(CallbackInfo ci) {
        if (loaded) return;
        loaded = true;
        ServerTranslations.onInitialize();
        INSTANCE = ServerLanguage.getInstance();
    }

    @Inject(method = "translate", cancellable = true, at = @At("RETURN"))
    private void server_translate(String string, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(((ServerTranslationStorage)INSTANCE).getServerTranslation(string));
    }

    @Inject(method = "hasTranslation", cancellable = true, at = @At("RETURN"))
    private void server_hasTranslation(String string, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(((ServerTranslationStorage)INSTANCE).hasServerTranslation(string));
    }

    @Inject(method = "getInstance", cancellable = true, at = @At("RETURN"))
    private static void server_GetInstance(CallbackInfoReturnable<Language> cir) {
        cir.setReturnValue(ServerLanguage.getInstance());
    }
}
