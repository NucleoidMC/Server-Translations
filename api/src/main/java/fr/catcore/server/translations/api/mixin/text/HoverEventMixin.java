package fr.catcore.server.translations.api.mixin.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.nbt.StackNbtLocalizer;
import fr.catcore.server.translations.api.text.LocalizableHoverEvent;
import net.minecraft.text.HoverEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HoverEvent.class)
public abstract class HoverEventMixin<T> implements LocalizableHoverEvent {
    @Shadow
    public abstract HoverEvent.Action<T> getAction();

    @Shadow
    @Nullable
    public abstract <T> T getValue(HoverEvent.Action<T> action);

    @Override
    public HoverEvent asLocalizedFor(LocalizationTarget target) {
        var action = this.getAction();
        var value = this.getValue(action);

        if (action == HoverEvent.Action.SHOW_ITEM) {
            var itemStack = ((HoverEvent.ItemStackContent) value).asStack();
            var localized = StackNbtLocalizer.localize(itemStack, itemStack.getNbt(), target);
            itemStack.setNbt(localized);
            return new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(itemStack));
        }

        return (HoverEvent) (Object) this;
    }

}