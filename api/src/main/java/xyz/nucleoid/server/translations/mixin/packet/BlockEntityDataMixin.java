package xyz.nucleoid.server.translations.mixin.packet;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
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

    @ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/network/PacketByteBuf;"))
    private NbtCompound stapi$translateNbt(NbtCompound compound) {
        if (SignNbtLocalizer.isSign(this.type) && compound != null) {
            var target = LocalizationTarget.forPacket();

            if (target != null) {
                return SignNbtLocalizer.translateNbt(compound, LocalizationTarget.forPacket());
            }
        }
        return compound;
    }
}
