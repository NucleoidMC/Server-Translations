package fr.catcore.server.translations.api.text;

import fr.catcore.server.translations.api.LocalizationTarget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public interface LocalizableTextContent {

    void visitSelfLocalized(LocalizedTextVisitor visitor, LocalizationTarget target, Text text, Style style);

}
