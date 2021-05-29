package fr.catcore.server.translations.api.nbt;

import fr.catcore.server.translations.api.ServerTranslations;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class NbtLocalizer {
    private static final String EMPTY_STRING = ServerTranslations.ID + ":empty";
    private static final NbtString EMPTY_NbtElement = NbtString.of(EMPTY_STRING);

    private final NbtCompound NbtElement;
    private boolean copiedNbtElement;

    private NbtCompound result;
    private NbtCompound revert;

    public NbtLocalizer(NbtCompound NbtElement) {
        this.NbtElement = NbtElement;
    }

    public static void applyRevert(NbtCompound NbtElement, NbtCompound revert) {
        for (String key : revert.getKeys()) {
            NbtElement revertNbtElement = Objects.requireNonNull(revert.get(key));
            if (!isEmptyNbtElement(revertNbtElement)) {
                NbtElement.put(key, revertNbtElement);
            } else {
                NbtElement.remove(key);
            }
        }
    }

    private static boolean isEmptyNbtElement(NbtElement NbtElement) {
        return NbtElement.getType() == NbtElement.STRING_TYPE && NbtElement.asString().equals(EMPTY_STRING);
    }

    public void set(String key, NbtElement NbtElement) {
        NbtCompound result = this.getOrCreateResultNbtElement();

        NbtElement previous = result.put(key, NbtElement);
        if (!Objects.equals(NbtElement, previous)) {
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
        } else if (this.NbtElement != null) {
            return this.NbtElement.contains(key, type);
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
            this.result = this.NbtElement != null ? this.NbtElement.copy() : null;
            this.copiedNbtElement = true;
        }

        return this.result;
    }

    @Nullable
    public NbtCompound getRevertNbtElement() {
        return this.revert;
    }
}
