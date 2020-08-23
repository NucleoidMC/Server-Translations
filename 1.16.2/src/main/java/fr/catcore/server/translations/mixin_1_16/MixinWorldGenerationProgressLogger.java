package fr.catcore.server.translations.mixin_1_16;

import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldGenerationProgressLogger.class)
public class MixinWorldGenerationProgressLogger {

    @ModifyArg(method = "stop", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0), index = 0)
    private String translated_time(String string, Object p0) {
        return new TranslatableText("text.translated_server.time", p0).getString();
    }
}
