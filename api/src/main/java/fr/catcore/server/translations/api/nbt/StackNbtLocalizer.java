package fr.catcore.server.translations.api.nbt;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizableText;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.Text;

public final class StackNbtLocalizer {
    private static final String TRANSLATED_TAG = "server_translated";

    public static CompoundTag localize(ItemStack stack, CompoundTag tag, LocalizationTarget target) {
        NbtLocalizer nbt = new NbtLocalizer(tag);

        translateDisplay(target, nbt);
        translateBook(target, nbt);
        translateItemName(stack, target, nbt);

        CompoundTag revertTag = nbt.getRevertTag();
        if (revertTag != null) {
            nbt.set(TRANSLATED_TAG, revertTag);
        }

        return nbt.getResultTag();
    }

    public static CompoundTag unlocalize(CompoundTag tag) {
        if (tag != null && tag.contains(TRANSLATED_TAG, NbtType.COMPOUND)) {
            CompoundTag revert = tag.getCompound(TRANSLATED_TAG);
            NbtLocalizer.applyRevert(tag, revert);
            tag.remove(TRANSLATED_TAG);
        }

        return tag;
    }

    private static void translateItemName(ItemStack stack, LocalizationTarget target, NbtLocalizer nbt) {
        boolean hasCustomName = nbt.contains("display", NbtType.COMPOUND)
                && nbt.getCompound("display").contains("Name", NbtType.STRING);

        if (!hasCustomName) {
            Text name = stack.getItem().getName(stack);
            Text localized = LocalizableText.asLocalizedFor(name, target);
            if (!name.equals(localized)) {
                addNameToTag(nbt, localized);
            }
        }
    }

    private static void translateDisplay(LocalizationTarget target, NbtLocalizer nbt) {
        if (nbt.contains("display", NbtType.COMPOUND)) {
            CompoundTag display = nbt.getCompound("display");
            translateCustomName(display, target);
            translateLore(display, target);

            nbt.set("display", display);
        }
    }

    private static void translateBook(LocalizationTarget target, NbtLocalizer nbt) {
        if (nbt.contains("pages", NbtType.LIST)) {
            ListTag pages = nbt.getList("pages", NbtType.STRING);
            for (int i = 0; i < pages.size(); i++) {
                String pageJson = pages.getString(i);
                pageJson = localizeTextJson(pageJson, target);
                pages.setTag(i, StringTag.of(pageJson));
            }

            nbt.set("pages", pages);
        }
    }

    private static void translateCustomName(CompoundTag display, LocalizationTarget target) {
        if (display.contains("Name", NbtType.STRING)) {
            display.putString("Name", localizeTextJson(display.getString("Name"), target));
        }
    }

    private static void translateLore(CompoundTag display, LocalizationTarget target) {
        if (display.contains("Lore", NbtType.LIST)) {
            ListTag loreList = display.getList("Lore", NbtType.STRING);
            for (int i = 0; i < loreList.size(); i++) {
                loreList.setTag(i, StringTag.of(localizeTextJson(loreList.getString(i), target)));
            }
        }
    }

    private static void addNameToTag(NbtLocalizer nbt, Text name) {
        name = removeItalicsFromCustomName(name);

        CompoundTag display;
        if (nbt.contains("display", NbtType.COMPOUND)) {
            display = nbt.getCompound("display");
        } else {
            display = new CompoundTag();
        }

        display.putString("Name", Text.Serializer.toJson(name));

        nbt.set("display", display);
    }

    private static Text removeItalicsFromCustomName(Text name) {
        if (!name.getStyle().isItalic()) {
            return name.shallowCopy().styled(style -> style.withItalic(false));
        }
        return name;
    }

    private static String localizeTextJson(String json, LocalizationTarget target) {
        Text text = Text.Serializer.fromJson(json);
        if (text == null) {
            return json;
        }

        Text localized = LocalizableText.asLocalizedFor(text, target);
        if (!localized.equals(text)) {
            return Text.Serializer.toJson(text);
        } else {
            return json;
        }
    }
}
