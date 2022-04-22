package fr.catcore.server.translations.mixin;

import net.minecraft.advancement.AdvancementManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AdvancementManager.class)
public class MixinAdvancementManager {

    @ModifyArg(method = "load", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0, remap = false), index = 0)
    private String translated_loaded(String string, Object p0) {
        return Text.translatable("text.translated_server.loaded.advancement", p0).getString();
    }
}
