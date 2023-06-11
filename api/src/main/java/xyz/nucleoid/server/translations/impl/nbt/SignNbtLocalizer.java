package xyz.nucleoid.server.translations.impl.nbt;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import xyz.nucleoid.server.translations.api.Localization;
import xyz.nucleoid.server.translations.api.LocalizationTarget;

public class SignNbtLocalizer {

    public static boolean isSign(BlockEntityType<?> type) {
        return type == BlockEntityType.SIGN || type == BlockEntityType.HANGING_SIGN;
    }

    public static NbtCompound translateNbt(NbtCompound nbtCompound, LocalizationTarget target) {
        var nbt = nbtCompound.copy();
        updateSide(nbt.getCompound("front_text"), target);
        updateSide(nbt.getCompound("back_text"), target);
        return nbt;
    }

    private static void updateSide(NbtCompound nbt, LocalizationTarget target) {
        if (nbt == null || nbt.isEmpty()) {
            return;
        }

        updateLines(nbt.getList("messages", NbtElement.STRING_TYPE), target);
        if (nbt.contains("filtered_messages", NbtElement.LIST_TYPE)) {
            updateLines(nbt.getList("filtered_messages", NbtElement.STRING_TYPE), target);
        }
    }

    private static void updateLines(NbtList messages, LocalizationTarget target) {
        for (int i = 0; i < messages.size(); i++) {
            messages.set(i, NbtString.of(Text.Serializer.toJson(Localization.text(Text.Serializer.fromLenientJson(messages.getString(i)), target))));
        }
    }
}
