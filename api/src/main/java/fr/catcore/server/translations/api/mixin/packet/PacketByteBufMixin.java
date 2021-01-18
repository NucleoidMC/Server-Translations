package fr.catcore.server.translations.api.mixin.packet;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizableText;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PacketByteBuf.class)
public class PacketByteBufMixin {
    private static final String TRANSLATED_TAG = "server_translated";

    @Redirect(
            method = "writeItemStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketByteBuf;writeCompoundTag(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/network/PacketByteBuf;"
            )
    )
    private PacketByteBuf writeItemStackTag(PacketByteBuf buf, CompoundTag tag, ItemStack stack) {
        LocalizationTarget target = LocalizationTarget.forPacket();
        if (target == null) {
            return buf.writeCompoundTag(tag);
        }

        boolean hasCustomName = false;
        boolean copied = false;

        if (tag != null && tag.contains("display", NbtType.COMPOUND)) {
            tag = tag.copy();
            copied = true;

            CompoundTag display = tag.getCompound("display");
            hasCustomName = this.translateCustomName(display);
            this.translateLore(display);
        }

        if (!hasCustomName) {
            Text name = stack.getItem().getName(stack);
            Text localized = LocalizableText.asLocalizedFor(name, target);
            if (!name.equals(localized)) {
                if (!copied && tag != null) tag = tag.copy();
                tag = this.addNameToTag(tag, localized);
            }
        }

        return buf.writeCompoundTag(tag);
    }

    private boolean translateCustomName(CompoundTag display) {
        if (display.contains("Name", NbtType.STRING)) {
            display.putString("Name", this.translateTextJson(display.getString("Name")));
            return true;
        } else {
            return false;
        }
    }

    private void translateLore(CompoundTag display) {
        if (display.contains("Lore", NbtType.LIST)) {
            ListTag loreList = display.getList("Lore", NbtType.STRING);
            for (int i = 0; i < loreList.size(); i++) {
                loreList.setTag(i, StringTag.of(this.translateTextJson(loreList.getString(i))));
            }
        }
    }

    private String translateTextJson(String json) {
        MutableText text = Text.Serializer.fromJson(json);
        return Text.Serializer.toJson(text);
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

        display.putString("Name", Text.Serializer.toJson(name));

        display.putBoolean(TRANSLATED_TAG, true);

        return tag;
    }

    @Unique
    private Text removeItalicsFromCustomName(Text name) {
        if (!name.getStyle().isItalic()) {
            return name.shallowCopy().styled(style -> style.withItalic(false));
        }
        return name;
    }

    @Inject(method = "readItemStack", at = @At(value = "RETURN", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void readItemStack(CallbackInfoReturnable<ItemStack> ci, int id, int count, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TRANSLATED_TAG, NbtType.BYTE)) {
            tag.getCompound("display").remove("Name");
            tag.remove(TRANSLATED_TAG);
        }
    }
}
