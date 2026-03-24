package xyz.nucleoid.server.translations.mixin;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.server.translations.impl.ServerTranslations;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public abstract class ServerConfigurationPacketListenerImplMixin extends ServerCommonPacketListenerImpl {
    public ServerConfigurationPacketListenerImplMixin(MinecraftServer server, Connection connection, CommonListenerCookie cookie) {
        super(server, connection, cookie);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void stapi$setDefaultLanguage(MinecraftServer server, Connection connection, CommonListenerCookie cookie, CallbackInfo ci) {
        PacketContext context = connection.getPacketContext();
        context.set(ServerTranslations.LANGUAGE_KEY, cookie.clientInformation().language());
    }

    @Inject(
        method = "handleClientInformation",
        at = @At("HEAD")
    )
    private void stapi$setPacketContextLang(ServerboundClientInformationPacket packet, CallbackInfo ci) {
        PacketContext context = this.connection.getPacketContext();
        context.set(ServerTranslations.LANGUAGE_KEY, packet.information().language());
    }
}
