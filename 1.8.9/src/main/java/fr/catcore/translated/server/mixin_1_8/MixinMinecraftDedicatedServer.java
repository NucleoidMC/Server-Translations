package fr.catcore.translated.server.mixin_1_8;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Arrays;

@Mixin(MinecraftDedicatedServer.class)
public class MixinMinecraftDedicatedServer {

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 0), index = 0)
    private String translated_startingServer(String string) {
        return new TranslatableText("text.translated_server.starting_version", string.replace("Starting minecraft server version ", "")).getString();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 1), index = 0)
    private String translated_loadingProperties(String string) {
        return new TranslatableText("text.translated_server.loading.properties", "null").getString();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 3), index = 0)
    private String translated_gamemode(String string) {
        return new TranslatableText("text.translated_server.loading.gamemode", new TranslatableText("gameMode." + LevelInfo.GameMode.valueOf(string.replace("Default game type: ","")).getName()).getString()).getString();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 4), index = 0)
    private String translated_generatingKeypair(String string) {
        return new TranslatableText("text.translated_server.generate.keypair", "null").getString();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 5), index = 0)
    private String translated_ip(String string) {
        String[] strings = string.replace("Starting Minecraft server on ","").split(":");
        return new TranslatableText("text.translated_server.loading.ip", strings[0], strings[1]).getString();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 6), index = 0)
    private String translated_preparingLevel(String string) {
        return new TranslatableText("text.translated_server.preparing.level", string.replace("Preparing level \"","").replace("\"","")).getString();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 7), index = 0)
    private String translated_done(String string) {
        return new TranslatableText("text.translated_server.done.old", string.replace("Done (","").replace(")! For help, type \"help\" or \"?\"","")).getString();
    }
}
