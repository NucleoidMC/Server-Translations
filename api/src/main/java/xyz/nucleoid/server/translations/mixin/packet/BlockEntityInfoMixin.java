package xyz.nucleoid.server.translations.mixin.packet;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.impl.nbt.SignNbtLocalizer;

import java.util.Optional;
import java.util.function.Function;

@Mixin(ClientboundLevelChunkPacketData.BlockEntityInfo.class)
public class BlockEntityInfoMixin {

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;)Lnet/minecraft/network/codec/StreamCodec;"))
    private static StreamCodec<RegistryFriendlyByteBuf, ClientboundLevelChunkPacketData.BlockEntityInfo> patchCodec(StreamCodec<RegistryFriendlyByteBuf, ClientboundLevelChunkPacketData.BlockEntityInfo> original) {
        return original.map(Function.identity(), info -> {
            if (SignNbtLocalizer.isSign(info.type()) && info.tag().isPresent()) {
                PacketContext context = PacketContext.get();
                if (context != null) {
                    var target = LocalizationTarget.forPacket();
                    MinecraftServer server = context.get(PacketContext.SERVER_INSTANCE);
                    if (target != null && server != null) {
                        return new ClientboundLevelChunkPacketData.BlockEntityInfo(info.packedXZ(), info.y(), info.type(),
                                Optional.of(SignNbtLocalizer.translateNbt(info.tag().orElseThrow(), target, server.registryAccess())));
                    }
                }
            }
            return info;
        });
    }
}
