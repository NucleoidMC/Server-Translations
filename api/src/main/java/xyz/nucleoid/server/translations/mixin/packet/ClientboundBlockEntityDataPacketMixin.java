package xyz.nucleoid.server.translations.mixin.packet;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Function3;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.impl.nbt.SignNbtLocalizer;

import java.util.function.Function;

@Mixin(ClientboundBlockEntityDataPacket.class)
public class ClientboundBlockEntityDataPacketMixin {
    @Invoker("<init>")
    static ClientboundBlockEntityDataPacket invokeInit(BlockPos pos, BlockEntityType<?> blockEntityType, CompoundTag tag) {
        throw new AssertionError();
    }

    @WrapOperation(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function3;)Lnet/minecraft/network/codec/StreamCodec;"
        )
    )
    private static StreamCodec<RegistryFriendlyByteBuf, ClientboundBlockEntityDataPacket> stapi$translateNbt(
        StreamCodec<ByteBuf, BlockPos> codec1,
        Function<ClientboundBlockEntityDataPacket, BlockPos> from1,
        StreamCodec<RegistryFriendlyByteBuf, BlockEntityType<?>> codec2,
        Function<ClientboundBlockEntityDataPacket, BlockEntityType<?>> from2,
        StreamCodec<ByteBuf, CompoundTag> codec3,
        Function<ClientboundBlockEntityDataPacket, CompoundTag> from3,
        Function3<BlockPos, BlockEntityType<?>, CompoundTag, ClientboundBlockEntityDataPacket> _to,
        Operation<StreamCodec<RegistryFriendlyByteBuf, ClientboundBlockEntityDataPacket>> original
    ) {
        StreamCodec<RegistryFriendlyByteBuf, ClientboundBlockEntityDataPacket> codec = original.call(codec1, from1, codec2, from2, codec3, from3, _to);
        return codec.map(Function.identity(), packet -> {
            PacketContext context = PacketContext.get();
            if (SignNbtLocalizer.isSign(packet.getType()) && context != null) {
                LocalizationTarget target = LocalizationTarget.forPacket();
                MinecraftServer server = context.get(PacketContext.SERVER_INSTANCE);
                if (target != null && server != null) {
                    CompoundTag nbtCompound = SignNbtLocalizer.translateNbt(packet.getTag(), target, server.registryAccess());
                    return invokeInit(packet.getPos(), packet.getType(), nbtCompound);
                }
            }
            return packet;
        });
    }

}
