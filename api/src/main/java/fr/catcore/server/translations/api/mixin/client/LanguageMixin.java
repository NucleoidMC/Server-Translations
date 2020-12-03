package fr.catcore.server.translations.api.mixin.client;

import fr.catcore.server.translations.api.ServerTranslations;
import fr.catcore.server.translations.api.resource.language.ServerLanguageDefinition;
import fr.catcore.server.translations.api.resource.language.SystemDelegatedLanguage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Language.class)
public class LanguageMixin {
    @ModifyVariable(method = "setInstance", at = @At("HEAD"), argsOnly = true)
    private static Language modifyLanguage(Language language) {
        String languageCode = MinecraftClient.getInstance().getLanguageManager().getLanguage().getCode();
        ServerLanguageDefinition languageDefinition = ServerTranslations.INSTANCE.getLanguageDefinition(languageCode);
        ServerTranslations.INSTANCE.setSystemLanguage(languageDefinition);

        SystemDelegatedLanguage delegated = SystemDelegatedLanguage.INSTANCE;
        delegated.setVanilla(language);

        return delegated;
    }
}
