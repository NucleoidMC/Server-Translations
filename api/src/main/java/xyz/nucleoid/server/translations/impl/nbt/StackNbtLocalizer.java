package xyz.nucleoid.server.translations.impl.nbt;

import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.LocalizableText;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;

public final class StackNbtLocalizer {
    private static final String TRANSLATED_TAG = "server_translated";

    // While stack is unused, it's kept for backward compatibility and future proofing
    public static NbtCompound localize(ItemStack stack, NbtCompound tag, LocalizationTarget target) {
        return localize(stack, tag, target.getLanguage());
    }

    public static NbtCompound localize(ItemStack stack, NbtCompound tag, ServerLanguage language) {
        if (tag == null) {
            return null;
        }

        try {
            NbtLocalizer nbt = new NbtLocalizer(tag);

            translateDisplay(language, nbt);
            translateBook(language, nbt);

            NbtCompound revertTag = nbt.getRevertNbtElement();
            if (revertTag != null) {
                tag.put(TRANSLATED_TAG, revertTag);
            }

            return nbt.getResultNbtElement();
        } catch (Exception e) {
            return tag;
        }
    }

    public static NbtCompound unlocalize(NbtCompound tag) {
        if (tag != null && tag.contains(TRANSLATED_TAG, NbtElement.COMPOUND_TYPE)) {
            NbtCompound revert = tag.getCompound(TRANSLATED_TAG);
            NbtLocalizer.applyRevert(tag, revert);
            tag.remove(TRANSLATED_TAG);
        }

        return tag;
    }

    private static void translateDisplay(ServerLanguage target, NbtLocalizer nbt) {
        if (nbt.contains("display", NbtElement.COMPOUND_TYPE)) {
            NbtCompound display = nbt.getCompound("display");
            translateCustomName(display, target);
            translateLore(display, target);

            nbt.set("display", display);
        }
    }

    private static void translateBook(ServerLanguage target, NbtLocalizer nbt) {
        if (nbt.contains("pages", NbtElement.LIST_TYPE)) {
            NbtList pages = nbt.getList("pages", NbtElement.STRING_TYPE);
            for (int i = 0; i < pages.size(); i++) {
                String pageJson = pages.getString(i);
                pageJson = localizeTextJson(pageJson, target);
                pages.setElement(i, NbtString.of(pageJson));
            }

            nbt.set("pages", pages);
        }
    }

    private static void translateCustomName(NbtCompound display, ServerLanguage target) {
        if (display.contains("Name", NbtElement.STRING_TYPE)) {
            display.putString("Name", localizeTextJson(display.getString("Name"), target));
        }
    }

    private static void translateLore(NbtCompound display, ServerLanguage target) {
        if (display.contains("Lore", NbtElement.LIST_TYPE)) {
            NbtList loreList = display.getList("Lore", NbtElement.STRING_TYPE);
            for (int i = 0; i < loreList.size(); i++) {
                loreList.setElement(i, NbtString.of(localizeTextJson(loreList.getString(i), target)));
            }
        }
    }

    private static String localizeTextJson(String json, ServerLanguage target) {
        Text text;
        try {
            text = Text.Serializer.fromLenientJson(json);
        } catch (Exception e) {
            text = null;
        }

        if (text == null) {
            return json;
        }

        Text localized = LocalizableText.asLocalizedFor(text, target, true);
        if (!localized.equals(text)) {
            return Text.Serializer.toJson(localized);
        } else {
            return json;
        }
    }
}
