package xyz.nucleoid.server.translations.mixin.packet;

import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.nucleoid.server.translations.impl.nbt.SignNbtLocalizer;

@Mixin(BlockEntityUpdateS2CPacket.class)
public class BlockEntityUpdateS2CPacketMixin {

    @Shadow @Final private BlockEntityType<?> blockEntityType;

    @Shadow @Final @Nullable private NbtCompound nbt;

    @ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeNbt(Lnet/minecraft/nbt/NbtElement;)Lnet/minecraft/network/PacketByteBuf;"))
    private NbtElement stapi$translateNbt(NbtElement nbt) {

        if (SignNbtLocalizer.isSign(this.blockEntityType) && nbt instanceof NbtCompound nbtCompound) {
            var target = LocalizationTarget.forPacket();

            if (target != null) {
                return SignNbtLocalizer.translateNbt(nbtCompound, LocalizationTarget.forPacket());
            }
        }
        return nbt;
    }
}
