package xyz.nucleoid.server.translations.api;

import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.LocalizableText;
import xyz.nucleoid.server.translations.impl.nbt.StackNbtLocalizer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public final class Localization {
    private Localization() {}

    @Nullable
    public static NbtCompound itemStackNbt(ItemStack stack, ServerPlayerEntity target) {
        return itemStackNbt(stack, (LocalizationTarget) target);
    }

    @Nullable
    public static NbtCompound itemStackNbt(ItemStack stack, LocalizationTarget target) {
        return itemStackNbt(stack, target.getLanguage());
    }

    @Nullable
    public static NbtCompound itemStackNbt(ItemStack stack, ServerLanguage language) {
        return StackNbtLocalizer.localize(stack, stack.getNbt(), language);
    }

    public static ItemStack itemStack(ItemStack stack, ServerPlayerEntity target) {
        return itemStack(stack, (LocalizationTarget) target);
    }

    public static ItemStack itemStack(ItemStack stack, LocalizationTarget target) {
        return itemStack(stack, target.getLanguage());
    }

    public static ItemStack itemStack(ItemStack stack, ServerLanguage language) {
        var copy = stack.copy();
        copy.setNbt(StackNbtLocalizer.localize(copy, copy.getNbt(), language));
        return copy;
    }

    public static Text text(Text text, ServerPlayerEntity target) {
        return text(text, (LocalizationTarget) target);
    }

    public static Text text(Text text, LocalizationTarget target) {
        return text(text, target.getLanguage());
    }

    public static Text text(Text text, ServerLanguage language) {
        return text(text, true, language);
    }

    public static Text text(Text text, boolean full, ServerPlayerEntity target) {
        return text(text, full, (LocalizationTarget) target);
    }

    public static Text text(Text text, boolean full, LocalizationTarget target) {
        return text(text, full, target.getLanguage());
    }

    public static Text text(Text text, boolean full, ServerLanguage language) {
        return LocalizableText.asLocalizedFor(text, language, full);
    }
}
