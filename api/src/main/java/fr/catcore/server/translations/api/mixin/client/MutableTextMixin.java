package fr.catcore.server.translations.api.mixin.client;

import fr.catcore.server.translations.api.resource.language.SystemDelegatedLanguage;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Language;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MutableText.class)
public class MutableTextMixin {
    @Shadow
    private OrderedText ordered;
    @Unique
    private Language stapi_cachedLanguageVanilla;

    @Redirect(
            method = "asOrderedText",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/text/MutableText;language:Lnet/minecraft/util/Language;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private Language markAsAlwaysTrue(MutableText mutableText) {
        return null;
    }

    @Inject(
            method = "asOrderedText",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/text/MutableText;ordered:Lnet/minecraft/text/OrderedText;",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void cancelIfEqual(CallbackInfoReturnable<OrderedText> cir) {
        var language = Language.getInstance();

        if (language instanceof SystemDelegatedLanguage delegatedLanguage && delegatedLanguage.getVanilla() != this.stapi_cachedLanguageVanilla) {
            this.stapi_cachedLanguageVanilla = delegatedLanguage.getVanilla();
        } else {
            cir.setReturnValue(this.ordered);
        }
    }
}
