package fr.catcore.server.translations.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @ModifyArg(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0), index = 0)
    private String translated_preparingDimension(String string, Object p0) {
        return new TranslatableText("text.translated_server.preparing.dimension", p0).getString();
    }

    @ModifyArg(method = "save", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", ordinal = 0), index = 0)
    private String translated_saveChunks(String string, Object p0, Object p1) {
        return new TranslatableText("text.translated_server.save.chunks", p0, p1).getString();
    }

    @ModifyArg(method = "shutdown", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 0), index = 0)
    private String translated_stopping(String string) {
        return new TranslatableText("commands.stop.stopping").getString();
    }

    @ModifyArg(method = "shutdown", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 1), index = 0)
    private String translated_savePlayers(String string) {
        return new TranslatableText("text.translated_server.save.players").getString();
    }

    @ModifyArg(method = "shutdown", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 2), index = 0)
    private String translated_saveWorlds(String string) {
        return new TranslatableText("text.translated_server.save.worlds").getString();
    }

    @ModifyArg(method = "loadDataPacks", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0), index = 0)
    private static String translated_reloadData(String string, Object p0) {
        return new TranslatableText("text.translated_server.new.datapack", p0).getString();
    }

    @ModifyArg(method = "loadDataPacks", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 0), index = 0)
    private static String translated_forcingVanilla(String string) {
        return new TranslatableText("text.translated_server.force").getString();
    }

    @ModifyArg(method = "generateKeyPair", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 0), index = 0)
    private String translated_generatingKeypair(String string) {
        return new TranslatableText("text.translated_server.generate.keypair").getString();
    }
}
