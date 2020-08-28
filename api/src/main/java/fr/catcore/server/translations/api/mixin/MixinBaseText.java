package fr.catcore.server.translations.api.mixin;

import fr.catcore.server.translations.api.LocalizableText;
import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.text.BaseText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BaseText.class)
public abstract class MixinBaseText implements LocalizableText {
    private LocalizationTarget target;

    @Override
    public void setTarget(LocalizationTarget target) {
        this.target = target;
    }

    @Override
    public LocalizationTarget getTarget() {
        return this.target;
    }

    @Override
    public Text asLocalizedFor(LocalizationTarget target) {
        return this;
    }
}
