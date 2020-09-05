package fr.catcore.server.translations.api.mixin.text;

import fr.catcore.server.translations.api.LocalizableText;
import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizedTextVisitor;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(TranslatableText.class)
public abstract class MixinTranslatableText implements LocalizableText, Text {
    @Shadow
    @Final
    private List<StringVisitable> translations;

    @Shadow
    protected abstract void updateTranslations();

    @Redirect(method = "updateTranslations", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Language;getInstance()Lnet/minecraft/util/Language;"))
    private Language getLanguage() {
        return this.getTargetLanguage();
    }

    @Override
    public void visitSelfLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style) {
        LocalizationTarget lastTarget = this.getTarget();

        try {
            this.setTarget(target);
            this.updateTranslations();

            for (StringVisitable translation : this.translations) {
                if (translation instanceof LocalizableText) {
                    ((LocalizableText) translation).visitLocalized(visitor, target, style);
                } else {
                    translation.visit(visitor.asGeneric(target, style));
                }
            }
        } finally {
            this.setTarget(lastTarget);
        }
    }
}
