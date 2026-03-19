package xyz.nucleoid.server.translations.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import xyz.nucleoid.server.translations.impl.ServerTranslations;
import xyz.nucleoid.server.translations.api.language.ServerLanguageDefinition;
import xyz.nucleoid.server.translations.impl.language.SystemDelegatedLanguage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Language.class)
public class LanguageMixin {
    @ModifyVariable(method = "inject", at = @At("HEAD"), argsOnly = true)
    private static Language stapi$modifyLanguage(Language language) {
        String languageCode = Minecraft.getInstance().getLanguageManager().getSelected();
        ServerLanguageDefinition languageDefinition = ServerTranslations.INSTANCE.getLanguageDefinition(languageCode);
        ServerTranslations.INSTANCE.setSystemLanguage(languageDefinition);

        return new SystemDelegatedLanguage(language);
    }
}
