package xyz.nucleoid.server.translations.mixin.packet;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.impl.nbt.SignNbtLocalizer;

@Mixin(targets = "net/minecraft/network/packet/s2c/play/ChunkData$BlockEntityData")
public class BlockEntityDataMixin {
    @Shadow @Final private BlockEntityType<?> type;

    @ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeNbt(Lnet/minecraft/nbt/NbtElement;)Lnet/minecraft/network/PacketByteBuf;"))
    private NbtElement stapi$translateNbt(NbtElement nbt) {
        if (SignNbtLocalizer.isSign(this.type) && nbt instanceof NbtCompound compound) {
            var target = LocalizationTarget.forPacket();

            if (target != null) {
                return SignNbtLocalizer.translateNbt(compound, LocalizationTarget.forPacket());
            }
        }
        return nbt;
    }
}
