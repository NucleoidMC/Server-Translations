package xyz.nucleoid.server.translations.mixin.text;

import xyz.nucleoid.server.translations.impl.LocalizableText;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MutableText.class)
public abstract class MutableTextMixin implements LocalizableText {
    private boolean stapi$isLocalized;

    @Override
    public boolean stapi$isLocalized() {
        return this.stapi$isLocalized;
    }

    @Override
    public void stapi$setLocalized(boolean value) {
        this.stapi$isLocalized = value;
    }
}
