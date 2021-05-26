package fr.catcore.server.translations.api.nbt;

import fr.catcore.server.translations.api.ServerTranslations;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class NbtLocalizer {
    private static final String EMPTY_STRING = ServerTranslations.ID + ":empty";
    private static final StringTag EMPTY_TAG = StringTag.of(EMPTY_STRING);

    private final CompoundTag tag;
    private boolean copiedTag;

    private CompoundTag result;
    private CompoundTag revert;

    public NbtLocalizer(CompoundTag tag) {
        this.tag = tag;
    }

    public static void applyRevert(CompoundTag tag, CompoundTag revert) {
        for (String key : revert.getKeys()) {
            Tag revertTag = Objects.requireNonNull(revert.get(key));
            if (!isEmptyTag(revertTag)) {
                tag.put(key, revertTag);
            } else {
                tag.remove(key);
            }
        }
    }

    private static boolean isEmptyTag(Tag tag) {
        return tag.getType() == NbtType.STRING && tag.asString().equals(EMPTY_STRING);
    }

    public void set(String key, Tag tag) {
        CompoundTag result = this.getOrCreateResultTag();

        Tag previous = result.put(key, tag);
        if (!Objects.equals(tag, previous)) {
            this.trackSet(key, previous);
        }
    }

    public CompoundTag getCompound(String key) {
        CompoundTag result = this.getResultTag();
        return result != null ? result.getCompound(key) : new CompoundTag();
    }

    public ListTag getList(String key, int type) {
        CompoundTag result = this.getResultTag();
        return result != null ? result.getList(key, type) : new ListTag();
    }

    public boolean contains(String key, int type) {
        if (this.result != null) {
            return this.result.contains(key, type);
        } else if (this.tag != null) {
            return this.tag.contains(key, type);
        } else {
            return false;
        }
    }

    private void trackSet(String key, Tag previous) {
        CompoundTag revert = this.revert;
        if (revert == null) {
            this.revert = revert = new CompoundTag();
        }

        if (!revert.contains(key)) {
            if (previous != null) {
                revert.put(key, previous);
            } else {
                revert.put(key, EMPTY_TAG);
            }
        }
    }

    private CompoundTag getOrCreateResultTag() {
        CompoundTag result = this.getResultTag();
        if (result == null) {
            this.result = result = new CompoundTag();
        }
        return result;
    }

    @Nullable
    public CompoundTag getResultTag() {
        if (!this.copiedTag) {
            this.result = this.tag != null ? this.tag.copy() : null;
            this.copiedTag = true;
        }

        return this.result;
    }

    @Nullable
    public CompoundTag getRevertTag() {
        return this.revert;
    }
}
