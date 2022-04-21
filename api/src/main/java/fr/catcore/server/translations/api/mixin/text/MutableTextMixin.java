package fr.catcore.server.translations.api.mixin.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizableHoverEvent;
import fr.catcore.server.translations.api.text.LocalizedTextVisitor;
import fr.catcore.server.translations.api.text.LocalizableText;
import fr.catcore.server.translations.api.text.LocalizableMutableText;
import net.minecraft.class_7417;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(MutableText.class)
public abstract class MutableTextMixin implements Text, LocalizableMutableText {

    @Shadow
    public abstract class_7417 method_10851();

    @Shadow
    public abstract List<Text> getSiblings();

    @Shadow
    public abstract Style getStyle();

    @Override
    public void visitLocalizedText(LocalizedTextVisitor visitor, LocalizationTarget target, Style style) {
        Style selfStyle = this.getStyle().withParent(style);
        var hoverEvent = selfStyle.getHoverEvent();
        if (hoverEvent != null) {
            var localizableHoverEvent = (LocalizableHoverEvent) hoverEvent;
            if (!localizableHoverEvent.shouldSkipLocalization()) {
                selfStyle = selfStyle.withHoverEvent(localizableHoverEvent.asLocalizedFor(target));
            }
        }

        if (this.method_10851() instanceof LocalizableText localizableText) {
            localizableText.visitSelfLocalized(visitor, target, selfStyle);
        } else {
            visitor.acceptLiteral(this.getString(), style);
        }

        for (Text sibling : this.getSiblings()) {
            if (sibling.method_10851() instanceof LocalizableText localizableText) {
                localizableText.visitLocalized(visitor, target, selfStyle);
            }
        }
    }

}
