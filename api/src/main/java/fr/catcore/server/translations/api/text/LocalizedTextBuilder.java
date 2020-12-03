package fr.catcore.server.translations.api.text;

import net.minecraft.text.MutableText;

public final class LocalizedTextBuilder implements LocalizedTextVisitor {
    private MutableText result;

    @Override
    public void accept(MutableText text) {
        if (this.result == null) {
            this.result = text;
        } else {
            this.result = this.result.append(text);
        }
    }

    public MutableText getResult() {
        return this.result;
    }
}
