package fr.catcore.server.translations.api.mixin.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.nbt.StackNbtLocalizer;
import fr.catcore.server.translations.api.text.LocalizableHoverEvent;
import fr.catcore.server.translations.api.text.LocalizableMutableText;
import net.minecraft.text.HoverEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HoverEvent.class)
public abstract class HoverEventMixin<T> implements LocalizableHoverEvent {
    @Shadow public abstract HoverEvent.Action<T> getAction();

    @Shadow @Nullable public abstract <T> T getValue(HoverEvent.Action<T> action);

    @Unique private boolean stapi_alreadyLocalized = false;

    @Override
    public HoverEvent asLocalizedFor(LocalizationTarget target) {
        var action = this.getAction();
        var value = this.getValue(action);

        if (action == HoverEvent.Action.SHOW_TEXT) {
            var text = ((LocalizableMutableText) value);

            if (text.shouldLocalize()) {
                return ((LocalizableHoverEvent) new HoverEvent(HoverEvent.Action.SHOW_TEXT, text.asLocalizedFor(target))).markAsLocalized();
            }
        } else if (action == HoverEvent.Action.SHOW_ENTITY) {
            var entity = (HoverEvent.EntityContent) value;
            var text = ((LocalizableMutableText)entity.name);

            if (text.shouldLocalize()) {
                return ((LocalizableHoverEvent) new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(entity.entityType, entity.uuid, text))).markAsLocalized();
            }
        } else if (action == HoverEvent.Action.SHOW_ITEM) {
            var itemStack = ((HoverEvent.ItemStackContent) value).asStack();
            var localized = StackNbtLocalizer.localize(itemStack, itemStack.getNbt(), target);
            itemStack.setNbt(localized);
            return ((LocalizableHoverEvent) new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(itemStack))).markAsLocalized();
        }

        return ((LocalizableHoverEvent) new HoverEvent(action, value)).markAsLocalized();
    }

    @Override
    public boolean shouldSkipLocalization() {
        return this.stapi_alreadyLocalized;
    }

    @Override
    public HoverEvent markAsLocalized() {
        this.stapi_alreadyLocalized = true;
        return (HoverEvent) (Object) this;
    }
}
