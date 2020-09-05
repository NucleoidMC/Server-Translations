package fr.catcore.server.translations.api.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;

public final class LocalizedLiteralBuilder implements LocalizedTextVisitor {
    private MutableText result;

    @Override
    public void accept(LocalizationTarget target, Style style, String string) {
        MutableText sibling = new LiteralText(string).setStyle(style);
        if (this.result == null) {
            this.result = sibling;
        } else {
            this.result = this.result.append(sibling);
        }
    }

    public MutableText getResult() {
        return this.result;
    }
}
