package fr.catcore.server.translations.api.mixin.client;

import fr.catcore.server.translations.api.resource.language.SystemDelegatedLanguage;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Language;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TranslatableTextContent.class)
public abstract class TranslatableTextContentMixin {
    @Unique
    private Language stapi_cachedLanguage = null;

    @Redirect(
            method = "updateTranslations",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/text/TranslatableTextContent;languageCache:Lnet/minecraft/util/Language;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private Language markAsAlwaysTrue(TranslatableTextContent translatableTextContent) {
        return null;
    }

    @Inject(
            method = "updateTranslations",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/text/TranslatableTextContent;languageCache:Lnet/minecraft/util/Language;",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void cancelIfEqual(CallbackInfo ci) {
        var language = Language.getInstance();

        if (language instanceof SystemDelegatedLanguage delegatedLanguage && delegatedLanguage.getVanilla() != this.stapi_cachedLanguage) {
            this.stapi_cachedLanguage = delegatedLanguage.getVanilla();
        } else {
            ci.cancel();
        }
    }
}
