package fr.catcore.server.translations.mixin_1_16;

import net.minecraft.advancement.AdvancementManager;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AdvancementManager.class)
public class MixinAdvancementManager {

    @ModifyArg(method = "load", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0), index = 0)
    private String translated_loaded(String string, Object p0) {
        return new TranslatableText("text.translated_server.loaded.advancement", p0).getString();
    }
}
