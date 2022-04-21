package fr.catcore.server.translations.api.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.text.Style;

public interface LocalizableText {

    void visitLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style);

    void visitSelfLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Style style);

}
