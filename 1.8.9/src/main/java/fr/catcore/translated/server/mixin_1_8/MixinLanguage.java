package fr.catcore.translated.server.mixin_1_8;

import fr.catcore.translated.server.TranslatedServerLog;
import fr.catcore.translated.server.resource.language.ServerLanguage;
import fr.catcore.translated.server.resource.language.ServerTranslationStorage;
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
    private void server_getInstance(CallbackInfo ci) {
        if (loaded) return;
        loaded = true;
        TranslatedServerLog.onInitialize();
        INSTANCE = ServerLanguage.getInstance();
    }

    @Inject(method = "method_4992", cancellable = true, at = @At("RETURN"))
    private void server_translate(String string, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(((ServerTranslationStorage)INSTANCE).getServerTranslation(string));
    }

    @Inject(method = "hasTranslation", cancellable = true, at = @At("RETURN"))
    private void server_hasTranslation(String string, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(((ServerTranslationStorage)INSTANCE).hasServerTranslation(string));
    }
}
