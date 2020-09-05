package fr.catcore.server.translations.api.mixin.text;

import fr.catcore.server.translations.api.LocalizableText;
import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizedTextVisitor;
import net.minecraft.text.BaseText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BaseText.class)
public abstract class MixinBaseText implements LocalizableText {
    private LocalizationTarget target;

    @Override
    public void setTarget(LocalizationTarget target) {
        this.target = target;
    }

    @Override
    public LocalizationTarget getTarget() {
        return this.target;
    }

    @Override
    public Text asLocalizedFor(LocalizationTarget target) {
        return this;
    }

    @Override
    public void visitLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style) {
        Style selfStyle = this.getStyle().withParent(style);
        this.visitSelfLocalized(visitor, target, selfStyle);

        for (Text sibling : this.getSiblings()) {
            if (sibling instanceof LocalizableText) {
                ((LocalizableText) sibling).visitLocalized(visitor, target, selfStyle);
            } else {
                sibling.visit(visitor.asGeneric(target, selfStyle));
            }
        }
    }

    @Override
    public void visitSelfLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style) {
        visitor.accept(target, style, this.asString());
    }
}
