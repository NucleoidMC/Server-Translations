package fr.catcore.server.translations.api.mixin.packet;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizableText;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PacketByteBuf.class)
public class PacketByteBufMixin {
    @Redirect(
            method = "writeItemStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketByteBuf;writeCompoundTag(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/network/PacketByteBuf;"
            )
    )
    private PacketByteBuf writeItemStackTag(PacketByteBuf buf, CompoundTag tag, ItemStack stack) {
        LocalizationTarget target = LocalizationTarget.forPacket();
        if (target == null || this.hasCustomName(tag)) {
            return buf.writeCompoundTag(tag);
        }

        Text name = stack.getName();
        Text localized = LocalizableText.asLocalizedFor(name, target);
        if (!name.equals(localized)) {
            tag = this.addNameToTag(tag, localized);
        }

        return buf.writeCompoundTag(tag);
    }

    @Unique
    private CompoundTag addNameToTag(CompoundTag tag, Text name) {
        name = this.removeItalicsFromCustomName(name);

        if (tag == null) {
            tag = new CompoundTag();
        }

        CompoundTag display;
        if (tag.contains("display", NbtType.COMPOUND)) {
            display = tag.getCompound("display");
        } else {
            tag.put("display", display = new CompoundTag());
        }

        // TODO: attach tag such that we know to strip custom name when receiving an itemstack from the client
        display.putString("Name", Text.Serializer.toJson(name));

        return tag;
    }

    @Unique
    private Text removeItalicsFromCustomName(Text name) {
        if (!name.getStyle().isItalic()) {
            return name.shallowCopy().styled(style -> style.withItalic(false));
        }
        return name;
    }

    @Unique
    private boolean hasCustomName(CompoundTag tag) {
        return tag != null && tag.contains("display", NbtType.COMPOUND) && tag.contains("Name", NbtType.STRING);
    }
}
