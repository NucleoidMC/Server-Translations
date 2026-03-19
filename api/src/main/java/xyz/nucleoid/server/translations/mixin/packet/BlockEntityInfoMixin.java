package xyz.nucleoid.server.translations.mixin.packet;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.impl.nbt.SignNbtLocalizer;

@Mixin(targets = "net/minecraft/network/protocol/game/ClientboundLevelChunkPacketData$BlockEntityInfo")
public class BlockEntityInfoMixin {
    @Shadow @Final private BlockEntityType<?> type;

    @ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/RegistryFriendlyByteBuf;writeNbt(Lnet/minecraft/nbt/Tag;)Lnet/minecraft/network/FriendlyByteBuf;"))
    private Tag stapi$translateNbt(Tag nbt) {
        if (SignNbtLocalizer.isSign(this.type) && nbt instanceof CompoundTag compound) {
            PacketContext context = PacketContext.get();
            if (context != null) {
                var target = LocalizationTarget.forPacket();
                MinecraftServer server = context.get(PacketContext.SERVER_INSTANCE);
                if (target != null && server != null) {
                    return SignNbtLocalizer.translateNbt(compound, target, server.registryAccess());
                }
            }
        }
        return nbt;
    }
}
