package fr.catcore.server.translations.api.mixin.client;

import fr.catcore.server.translations.api.resource.language.SystemDelegatedLanguage;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;
import net.minecraft.text.TranslationException;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TranslatableText.class)
public abstract class TranslatableTextMixin {
    @Unique
    private Language stapi_cachedLanguage = null;

    @Redirect(
            method = "updateTranslations",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/text/TranslatableText;languageCache:Lnet/minecraft/util/Language;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private Language markAsAlwaysTrue(TranslatableText translatableText) {
        return null;
    }

    @Inject(
            method = "updateTranslations",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/text/TranslatableText;languageCache:Lnet/minecraft/util/Language;",
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
