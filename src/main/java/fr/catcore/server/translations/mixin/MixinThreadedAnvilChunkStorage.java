package fr.catcore.server.translations.mixin;

import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ThreadedAnvilChunkStorage.class)
public class MixinThreadedAnvilChunkStorage {

//    @ModifyArg(method = "save(Z)V", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0, remap = false), index = 0)
//    private String translated_reloadData(String string, Object p0) {
//        return Text.translatable("text.translated_server.saved", p0).getString();
//    }
}
