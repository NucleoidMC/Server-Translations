package fr.catcore.server.translations.mixin_1_16;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MinecraftDedicatedServer.class)
public class MixinMinecraftDedicatedServer {

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 0), index = 0)
    private String translated_startingServer(String string) {
        return new TranslatableText("text.translated_server.starting_version", string.replace("Starting minecraft server version ", "")).getString();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 1), index = 0)
    private String translated_loadingProperties(String string) {
        return new TranslatableText("text.translated_server.loading.properties").getString();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0), index = 0)
    private String translated_gamemode(String string, Object p0) {
        return new TranslatableText("text.translated_server.loading.gamemode", ((GameMode)p0).getTranslatableName().getString()).getString();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 2), index = 0)
    private String translated_generatingKeypair(String string) {
        return new TranslatableText("text.translated_server.generate.keypair").getString();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", ordinal = 0), index = 0)
    private String translated_ip(String string, Object p0, Object p1) {
        return new TranslatableText("text.translated_server.loading.ip", p0, p1).getString();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 1), index = 0)
    private String translated_preparingLevel(String string, Object p0) {
        return new TranslatableText("text.translated_server.preparing.level", p0).getString();
    }

    @ModifyArg(method = "setupServer", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 2), index = 0)
    private String translated_done(String string, Object p0) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return new TranslatableText("text.translated_server.done", p0).getString();
        } else return string;
    }
}
