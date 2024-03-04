package xyz.nucleoid.server.translations.mixin.packet;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Function3;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.impl.nbt.SignNbtLocalizer;

import java.util.function.Function;

@Mixin(BlockEntityUpdateS2CPacket.class)
public class BlockEntityUpdateS2CPacketMixin {

    @Invoker("<init>")
    static BlockEntityUpdateS2CPacket invokeInit(BlockPos pos, BlockEntityType<?> blockEntityType, NbtCompound nbt) {
        return null;
    }

    @WrapOperation(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/codec/PacketCodec;tuple(Lnet/minecraft/network/codec/PacketCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/PacketCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/PacketCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function3;)Lnet/minecraft/network/codec/PacketCodec;"
        )
    )
    private static PacketCodec<RegistryByteBuf, BlockEntityUpdateS2CPacket> stapi$translateNbt(
        PacketCodec<ByteBuf, BlockPos> codec1,
        Function<BlockEntityUpdateS2CPacket, BlockPos> from1,
        PacketCodec<RegistryByteBuf, BlockEntityType<?>> codec2,
        Function<BlockEntityUpdateS2CPacket, BlockEntityType<?>> from2,
        PacketCodec<ByteBuf, NbtCompound> codec3,
        Function<BlockEntityUpdateS2CPacket, NbtCompound> from3,
        Function3<BlockPos, BlockEntityType<?>, NbtCompound, BlockEntityUpdateS2CPacket> _to,
        Operation<PacketCodec<RegistryByteBuf, BlockEntityUpdateS2CPacket>> original
    ) {
        PacketCodec<RegistryByteBuf, BlockEntityUpdateS2CPacket> codec = original.call(codec1, from1, codec2, from2, codec3, from3, _to);
        return codec.xmap(Function.identity(), packet -> {
            if (SignNbtLocalizer.isSign(packet.getBlockEntityType())) {
                var target = LocalizationTarget.forPacket();

                if (target != null) {
                    NbtCompound nbtCompound = SignNbtLocalizer.translateNbt(packet.getNbt(), LocalizationTarget.forPacket());
                    return invokeInit(packet.getPos(), packet.getBlockEntityType(), nbtCompound);
                }
            }
            return packet;
        });
    }

}
