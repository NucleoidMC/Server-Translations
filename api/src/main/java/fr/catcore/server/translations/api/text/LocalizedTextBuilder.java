package fr.catcore.server.translations.api.text;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public final class LocalizedTextBuilder implements LocalizedTextVisitor {
    private MutableText result;
    private boolean first;

    @Override
    public void accept(Text text) {
        if (this.result == null) {
            this.acceptFirst(text);
        } else {
            this.acceptSibling(text);
        }
    }

    private void acceptFirst(Text text) {
        if (text instanceof MutableText) {
            this.result = (MutableText) text;
            this.first = true;
        } else {
            this.result = text.shallowCopy();
        }
    }

    private void acceptSibling(Text text) {
        if (this.first) {
            this.result = new LiteralText("").append(this.result);
            this.first = false;
        }
        this.result = this.result.append(text);
    }

    public MutableText getResult() {
        return this.result;
    }
}
