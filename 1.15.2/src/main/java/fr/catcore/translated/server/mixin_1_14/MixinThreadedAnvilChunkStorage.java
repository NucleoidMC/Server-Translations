package fr.catcore.translated.server.mixin_1_14;

import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ThreadedAnvilChunkStorage.class)
public class MixinThreadedAnvilChunkStorage {



    @ModifyArg(method = "save(Z)V", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0), index = 0)
    private String translated_reloadData(String string, Object p0) {
        return new TranslatableText("text.translated_server.saved", p0).getString();
    }
}
