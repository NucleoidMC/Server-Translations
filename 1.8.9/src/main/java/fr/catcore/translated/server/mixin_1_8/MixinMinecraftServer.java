package fr.catcore.translated.server.mixin_1_8;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @ModifyArg(method = "prepareWorlds", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V"), index = 0)
    private String translated_preparingDimension(String string) {
        return new TranslatableText("text.translated_server.preparing.dimension.old", string.replace("Preparing start region for level ","")).getString();
    }

    @ModifyArg(method = "saveWorlds", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 0), index = 0)
    private String translated_saveChunks(String string) {
        return new TranslatableText("text.translated_server.save.chunks", string.replace("Saving chunks for level '","").split("'/")[0], string.replace("Saving chunks for level '","").split("'/")[1]).getString();
    }

    @ModifyArg(method = "stopWorlds", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 0), index = 0)
    private String translated_stopping(String string) {
        return new TranslatableText("commands.stop.stopping", "null").getString();
    }

    @ModifyArg(method = "stopWorlds", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 1), index = 0)
    private String translated_savePlayers(String string) {
        return new TranslatableText("text.translated_server.save.players", "null").getString();
    }

    @ModifyArg(method = "stopWorlds", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 2), index = 0)
    private String translated_saveWorlds(String string) {
        return new TranslatableText("text.translated_server.save.worlds", "null").getString();
    }

    @ModifyArg(method = "logProgress", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 0), index = 0)
    private String translated_progress(String progressType) {
        if (progressType.startsWith("Preparing spawn area")) return new TranslatableText("text.translated_server.progress.old", progressType.replace("Preparing spawn area: ", "").replace("%", "")).getString();
        return progressType;
    }
}
