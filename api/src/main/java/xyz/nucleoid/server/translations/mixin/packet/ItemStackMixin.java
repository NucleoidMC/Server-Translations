package xyz.nucleoid.server.translations.mixin.packet;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.impl.nbt.StackNbtLocalizer;

@Mixin(targets = "net/minecraft/item/ItemStack$1")
public abstract class ItemStackMixin {

    @WrapOperation(
        method = "encode(Lnet/minecraft/network/RegistryByteBuf;Lnet/minecraft/item/ItemStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getNbt()Lnet/minecraft/nbt/NbtCompound;"
        )
    )
    private static NbtCompound stapi$encodeItemStackTag(ItemStack itemStack, Operation<NbtCompound> original) {
        NbtCompound tag = original.call(itemStack);
        LocalizationTarget target = LocalizationTarget.forPacket();
        if (target != null) {
            return StackNbtLocalizer.localize(itemStack, tag, target);
        }
        return tag;
    }

    @WrapOperation(
        method = "decode(Lnet/minecraft/network/RegistryByteBuf;)Lnet/minecraft/item/ItemStack;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/PacketByteBuf;readNbt(Lio/netty/buffer/ByteBuf;)Lnet/minecraft/nbt/NbtCompound;"
        )
    )
    private static NbtCompound stapi$decodeItemStackTag(ByteBuf buf, Operation<NbtCompound> original) {
        NbtCompound tag = original.call(buf);
        return StackNbtLocalizer.unlocalize(tag);
    }

}
