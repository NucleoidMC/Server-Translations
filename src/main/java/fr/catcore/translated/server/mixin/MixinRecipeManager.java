package fr.catcore.translated.server.mixin;

import net.minecraft.recipe.RecipeManager;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {

    @ModifyArg(method = "apply", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0), index = 0)
    private String translated_loaded(String string, Object p0) {
        return new TranslatableText("text.translated_server.loaded.recipe", p0).getString();
    }
}
