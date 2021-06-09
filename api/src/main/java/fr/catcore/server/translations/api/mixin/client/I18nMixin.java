package fr.catcore.server.translations.api.mixin.client;

import fr.catcore.server.translations.api.resource.language.SystemDelegatedLanguage;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.IllegalFormatException;

@Mixin(I18n.class)
public class I18nMixin {

    /**
     * @author CatCore
     */
    @Overwrite
    public static String translate(String key, Object... args) {
        String string = SystemDelegatedLanguage.INSTANCE.get(key);

        try {
            return String.format(string, args);
        } catch (IllegalFormatException var4) {
            return "Format error: " + string;
        }
    }

    /**
     * @author CatCore
     */
    @Overwrite
    public static boolean hasTranslation(String key) {
        return SystemDelegatedLanguage.INSTANCE.hasTranslation(key);
    }
}
