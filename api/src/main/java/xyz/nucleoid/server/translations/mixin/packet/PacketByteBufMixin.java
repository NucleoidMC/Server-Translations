package xyz.nucleoid.server.translations.mixin.packet;

import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.impl.nbt.StackNbtLocalizer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PacketByteBuf.class)
public class PacketByteBufMixin {
    @Unique
    private ItemStack stapi_cachedStack;

    @Inject(
            method = "writeItemStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketByteBuf;writeNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/network/PacketByteBuf;",
                    shift = At.Shift.BEFORE
            )
    )
    private void cacheStack(ItemStack stack, CallbackInfoReturnable<PacketByteBuf> cir) {
        this.stapi_cachedStack = stack;
    }

    @ModifyArg(
            method = "writeItemStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketByteBuf;writeNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/network/PacketByteBuf;"
            )
    )
    private NbtCompound writeItemStackTag(NbtCompound tag) {
        LocalizationTarget target = LocalizationTarget.forPacket();
        if (target != null) {
            tag = StackNbtLocalizer.localize(this.stapi_cachedStack, tag, target);
        }
        this.stapi_cachedStack = null;

        return tag;
    }

    @Inject(method = "readItemStack", at = @At(value = "RETURN", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void readItemStack(CallbackInfoReturnable<ItemStack> ci, Item item, int count, ItemStack stack) {
        NbtCompound tag = StackNbtLocalizer.unlocalize(stack.getNbt());
        stack.setNbt(tag);
    }
}
