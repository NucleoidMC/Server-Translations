package fr.catcore.server.translations.api.mixin.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(MutableText.class)
public abstract class MutableTextMixin implements LocalizableText {

    private boolean sta$isLocalized = false;

    @Shadow
    public abstract List<Text> getSiblings();

    @Shadow
    public abstract Style getStyle();

    @Shadow
    public abstract TextContent getContent();

    @Shadow @Final private List<Text> siblings;

    @Override
    public void visitText(LocalizedTextVisitor visitor, LocalizationTarget target, Style style) {
        Style selfStyle = this.getStyle().withParent(style);
        var hoverEvent = selfStyle.getHoverEvent();
        if (hoverEvent != null) {
            var localizableHoverEvent = (LocalizableHoverEvent) hoverEvent;
            selfStyle = selfStyle.withHoverEvent(localizableHoverEvent.asLocalizedFor(target));
        }

        var vis = new LocalizedTextBuilder();

        if (this.getContent() instanceof LocalizableTextContent localizableTextContent) {
            localizableTextContent.visitSelfLocalized(vis, target, this, selfStyle);
        } else {
            vis.accept(this.copyContentOnly().setStyle(selfStyle));
        }

        for (var sibling : this.siblings) {
            ((LocalizableText) sibling).visitText(vis, target, Style.EMPTY);
        }

        visitor.accept((MutableText) vis.getResult());
    }

    @Override
    public boolean isLocalized() {
        return this.sta$isLocalized;
    }

    @Override
    public void setLocalized(boolean value) {
        this.sta$isLocalized = value;
    }
}
