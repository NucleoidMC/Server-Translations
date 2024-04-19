package xyz.nucleoid.server.translations.api;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.LocalizableText;

public final class Localization {
    private Localization() {}

    public static Text text(Text text, ServerPlayerEntity target) {
        return text(text, LocalizationTarget.of(target));
    }

    public static Text text(Text text, LocalizationTarget target) {
        return text(text, target.getLanguage());
    }

    public static Text text(Text text, ServerLanguage language) {
        return LocalizableText.asLocalizedFor(text, language);
    }

    @Nullable
    public static String raw(String key, ServerPlayerEntity target) {
        return raw(key, LocalizationTarget.of(target));
    }

    @Nullable
    public static String raw(String key, LocalizationTarget target) {
        return raw(key, target.getLanguage());
    }

    @Nullable
    public static String raw(String key, ServerLanguage language) {
        return language.serverTranslations().getOrNull(key);
    }
}
