package xyz.nucleoid.server.translations.impl.nbt;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import xyz.nucleoid.server.translations.api.Localization;
import xyz.nucleoid.server.translations.api.LocalizationTarget;

public class SignNbtLocalizer {

    public static boolean isSign(BlockEntityType<?> type) {
        return type == BlockEntityType.SIGN || type == BlockEntityType.HANGING_SIGN;
    }

    public static NbtCompound translateNbt(NbtCompound nbtCompound, LocalizationTarget target, RegistryWrapper.WrapperLookup lookup) {
        var nbt = nbtCompound.copy();
        updateSide(nbt.getCompoundOrEmpty("front_text"), target, lookup);
        updateSide(nbt.getCompoundOrEmpty("back_text"), target, lookup);
        return nbt;
    }

    private static void updateSide(NbtCompound nbt, LocalizationTarget target, RegistryWrapper.WrapperLookup lookup) {
        if (nbt == null || nbt.isEmpty()) {
            return;
        }

        updateLines(nbt.getListOrEmpty("messages"), lookup);
        if (nbt.contains("filtered_messages")) {
            updateLines(nbt.getListOrEmpty("filtered_messages"), lookup);
        }
    }

    private static void updateLines(NbtList messages, RegistryWrapper.WrapperLookup lookup) {
        var ops = lookup.getOps(NbtOps.INSTANCE);
        for (int i = 0; i < messages.size(); i++) {
            var data = messages.get(i);
            if (!(data instanceof NbtString)) {
                messages.set(i, TextCodecs.CODEC.encodeStart(ops, TextCodecs.CODEC.decode(ops, data).getOrThrow().getFirst()).getOrThrow());
            }
        }
    }
}
