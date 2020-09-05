package fr.catcore.server.translations.api.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

import java.util.Optional;

public interface LocalizedTextVisitor {
    void accept(LocalizationTarget target, Style style, String string);

    default <T> StringVisitable.Visitor<T> asGeneric(LocalizationTarget target, Style style) {
        return string -> {
            this.accept(target, style, string);
            return Optional.empty();
        };
    }
}
