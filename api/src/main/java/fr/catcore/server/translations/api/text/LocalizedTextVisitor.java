package fr.catcore.server.translations.api.text;

import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.Optional;

public interface LocalizedTextVisitor {
    void accept(MutableText text);

    default void acceptLiteral(String string, Style style) {
        this.accept(Text.literal(string).setStyle(style));
    }

    default <T> StringVisitable.Visitor<T> asGeneric(Style style) {
        return string -> {
            this.acceptLiteral(string, style);
            return Optional.empty();
        };
    }

    Text getResult();
}
