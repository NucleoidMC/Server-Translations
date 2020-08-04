package fr.catcore.translated.server.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @ModifyArg(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0), index = 0)
    private String translated_preparingDimension(String string, Object p0) {
        return new TranslatableText("text.translated_server.preparing.dimension", p0).getString();
    }
}
