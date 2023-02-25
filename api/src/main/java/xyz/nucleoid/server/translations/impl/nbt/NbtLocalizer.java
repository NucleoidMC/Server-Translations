package xyz.nucleoid.server.translations.impl.nbt;

import xyz.nucleoid.server.translations.impl.ServerTranslations;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class NbtLocalizer {
    private static final String EMPTY_STRING = ServerTranslations.ID + ":empty";
    private static final NbtString EMPTY_NbtElement = NbtString.of(EMPTY_STRING);

    private final NbtCompound nbtElement;
    private boolean copiedNbtElement;

    private NbtCompound result;
    private NbtCompound revert;

    public NbtLocalizer(NbtCompound nbtElement) {
        this.nbtElement = nbtElement;
    }

    public static void applyRevert(NbtCompound nbtElement, NbtCompound revert) {
        for (String key : revert.getKeys()) {
            NbtElement revertNbtElement = Objects.requireNonNull(revert.get(key));
            if (!isEmptyNbtElement(revertNbtElement)) {
                nbtElement.put(key, revertNbtElement);
            } else {
                nbtElement.remove(key);
            }
        }
    }

    private static boolean isEmptyNbtElement(NbtElement nbtElement) {
        return nbtElement.getType() == NbtElement.STRING_TYPE && nbtElement.asString().equals(EMPTY_STRING);
    }

    public void set(String key, NbtElement nbtElement) {
        NbtCompound result = this.getOrCreateResultNbtElement();

        NbtElement previous = result.put(key, nbtElement);
        if (!Objects.equals(nbtElement, previous)) {
            this.trackSet(key, previous);
        }
    }

    public NbtCompound getCompound(String key) {
        NbtCompound result = this.getResultNbtElement();
        return result != null ? result.getCompound(key) : new NbtCompound();
    }

    public NbtList getList(String key, int type) {
        NbtCompound result = this.getResultNbtElement();
        return result != null ? result.getList(key, type) : new NbtList();
    }

    public boolean contains(String key, int type) {
        if (this.result != null) {
            return this.result.contains(key, type);
        } else if (this.nbtElement != null) {
            return this.nbtElement.contains(key, type);
        } else {
            return false;
        }
    }

    private void trackSet(String key, NbtElement previous) {
        NbtCompound revert = this.revert;
        if (revert == null) {
            this.revert = revert = new NbtCompound();
        }

        if (!revert.contains(key)) {
            if (previous != null) {
                revert.put(key, previous);
            } else {
                revert.put(key, EMPTY_NbtElement);
            }
        }
    }

    private NbtCompound getOrCreateResultNbtElement() {
        NbtCompound result = this.getResultNbtElement();
        if (result == null) {
            this.result = result = new NbtCompound();
        }
        return result;
    }

    @Nullable
    public NbtCompound getResultNbtElement() {
        if (!this.copiedNbtElement) {
            this.result = this.nbtElement != null ? this.nbtElement.copy() : null;
            this.copiedNbtElement = true;
        }

        return this.result;
    }

    @Nullable
    public NbtCompound getRevertNbtElement() {
        return this.revert;
    }
}
