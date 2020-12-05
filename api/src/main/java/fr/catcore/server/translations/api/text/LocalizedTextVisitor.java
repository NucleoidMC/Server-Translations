package fr.catcore.server.translations.api.text;

import net.minecraft.text.*;

import java.util.Optional;

public interface LocalizedTextVisitor {
    void accept(MutableText text);

    default void acceptLiteral(String string, Style style) {
        this.accept(new LiteralText(string).setStyle(style));
    }

    default <T> StringVisitable.Visitor<T> asGeneric(Style style) {
        return string -> {
            this.acceptLiteral(string, style);
            return Optional.empty();
        };
    }
}
