package fr.catcore.server.translations.api.mixin;

import fr.catcore.server.translations.api.LocalizableText;
import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.text.*;
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
    public List<StringVisitable> translations;

    @Shadow
    protected abstract void updateTranslations();

    @Override
    public Text asLocalizedFor(LocalizationTarget target) {
        LocalizationTarget lastTarget = this.getTarget();

        try {
            this.setTarget(target);
            this.updateTranslations();

            MutableText literal = this.selfAsLiteral(target);

            for (Text sibling : this.getSiblings()) {
                sibling = LocalizableText.asLocalizedFor(sibling, target);
                if (literal == null) {
                    literal = sibling.shallowCopy();
                } else {
                    literal = literal.append(sibling);
                }
            }

            return literal != null ? literal : LiteralText.EMPTY;
        } finally {
            this.setTarget(lastTarget);
        }
    }

    @Redirect(method = "updateTranslations", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Language;getInstance()Lnet/minecraft/util/Language;"))
    private Language getLanguage() {
        return this.getTargetLanguage();
    }

    private MutableText selfAsLiteral(LocalizationTarget target) {
        MutableText literal = null;

        for (StringVisitable entry : this.translations) {
            Text text;
            if (entry instanceof Text) {
                text = LocalizableText.asLocalizedFor((Text) entry, target);
            } else {
                text = new LiteralText(entry.getString());
            }

            if (literal == null) {
                literal = text.shallowCopy();
            } else {
                literal = literal.append(text);
            }
        }

        return literal;
    }
}
