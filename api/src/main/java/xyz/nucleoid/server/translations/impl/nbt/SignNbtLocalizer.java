package xyz.nucleoid.server.translations.impl.nbt;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xyz.nucleoid.server.translations.api.LocalizationTarget;

public class SignNbtLocalizer {

    public static boolean isSign(BlockEntityType<?> type) {
        return type == BlockEntityType.SIGN || type == BlockEntityType.HANGING_SIGN;
    }

    public static CompoundTag translateNbt(CompoundTag compoundTag, LocalizationTarget target, HolderLookup.Provider lookup) {
        var nbt = compoundTag.copy();
        updateSide(nbt.getCompoundOrEmpty("front_text"), target, lookup);
        updateSide(nbt.getCompoundOrEmpty("back_text"), target, lookup);
        return nbt;
    }

    private static void updateSide(CompoundTag tag, LocalizationTarget target, HolderLookup.Provider lookup) {
        if (tag == null || tag.isEmpty()) {
            return;
        }

        updateLines(tag.getListOrEmpty("messages"), lookup);
        if (tag.contains("filtered_messages")) {
            updateLines(tag.getListOrEmpty("filtered_messages"), lookup);
        }
    }

    private static void updateLines(ListTag messages, HolderLookup.Provider lookup) {
        var ops = lookup.createSerializationContext(NbtOps.INSTANCE);
        for (int i = 0; i < messages.size(); i++) {
            var data = messages.get(i);
            if (!(data instanceof StringTag)) {
                Component decoded = ComponentSerialization.CODEC.parse(ops, data).getOrThrow();
                messages.set(i, ComponentSerialization.CODEC.encodeStart(ops, decoded).getOrThrow());
            }
        }
    }
}
