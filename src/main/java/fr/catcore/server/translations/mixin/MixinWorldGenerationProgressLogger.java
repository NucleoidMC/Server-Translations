package fr.catcore.server.translations.mixin;

import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldGenerationProgressLogger.class)
public class MixinWorldGenerationProgressLogger {

    @ModifyArg(method = "stop", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0, remap = false), index = 0)
    private String translated_time(String string, Object p0) {
        return Text.translatable("text.translated_server.time", p0).getString();
    }
}
