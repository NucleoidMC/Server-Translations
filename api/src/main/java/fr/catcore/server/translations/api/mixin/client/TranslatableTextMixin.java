package fr.catcore.server.translations.api.mixin.client;

import fr.catcore.server.translations.api.resource.language.SystemDelegatedLanguage;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;
import net.minecraft.text.TranslationException;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(TranslatableText.class)
public abstract class TranslatableTextMixin {

    @Shadow @Nullable private Language languageCache;

    @Shadow @Final private List<StringVisitable> translations;

    @Shadow @Final private String key;

    @Shadow protected abstract void setTranslation(String translation);

    /**
     * @author CatCore
     */
    @Overwrite
    private void updateTranslations() {
        Language language = Language.getInstance();
        if (language instanceof SystemDelegatedLanguage delegatedLanguage && delegatedLanguage.languageChanged()) {
            this.languageCache = language;
            this.translations.clear();
            String string = language.get(this.key);

            try {
                this.setTranslation(string);
            } catch (TranslationException var4) {
                this.translations.clear();
                this.translations.add(StringVisitable.plain(string));
            }
        }
    }
}
