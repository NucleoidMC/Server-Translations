package fr.catcore.server.translations.api.mixin.packet;

import fr.catcore.server.translations.api.nbt.StackNbtLocalizer;
import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PacketByteBuf.class)
public class PacketByteBufMixin {
    @Redirect(
            method = "writeItemStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketByteBuf;writeNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/network/PacketByteBuf;"
            )
    )
    private PacketByteBuf writeItemStackTag(PacketByteBuf buf, NbtCompound tag, ItemStack stack) {
        LocalizationTarget target = LocalizationTarget.forPacket();
        if (target != null) {
            tag = StackNbtLocalizer.localize(stack, tag, target);
        }

        return buf.writeNbt(tag);
    }

    @Inject(method = "readItemStack", at = @At(value = "RETURN", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void readItemStack(CallbackInfoReturnable<ItemStack> ci, int id, int count, ItemStack stack) {
        NbtCompound tag = StackNbtLocalizer.unlocalize(stack.getNbt());
        stack.setNbt(tag);
    }
}
