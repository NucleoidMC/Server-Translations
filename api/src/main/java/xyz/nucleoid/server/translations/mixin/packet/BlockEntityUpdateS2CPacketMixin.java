package xyz.nucleoid.server.translations.mixin.packet;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.server.translations.api.Localization;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.nucleoid.server.translations.impl.ServerTranslations;
import xyz.nucleoid.server.translations.impl.nbt.SignNbtLocalizer;

@Mixin(BlockEntityUpdateS2CPacket.class)
public class BlockEntityUpdateS2CPacketMixin {

    @Shadow @Final private BlockEntityType<?> blockEntityType;

    @ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/network/PacketByteBuf;"))
    private NbtCompound stapi$translateNbt(NbtCompound nbtCompound) {

        if (SignNbtLocalizer.isSign(this.blockEntityType) && nbtCompound != null) {
            var target = LocalizationTarget.forPacket();

            if (target != null) {
                return SignNbtLocalizer.translateNbt(nbtCompound, LocalizationTarget.forPacket());
            }
        }
        return nbtCompound;
    }
}
