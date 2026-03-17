package xyz.nucleoid.server.translations.api;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.LocalizableComponent;

public final class Localization {
    private Localization() {}

    public static Component text(Component text, ServerPlayer target) {
        return text(text, LocalizationTarget.of(target));
    }

    public static Component text(Component text, LocalizationTarget target) {
        return text(text, target.getLanguage());
    }

    public static Component text(Component text, ServerLanguage language) {
        return LocalizableComponent.asLocalizedFor(text, language);
    }

    @Nullable
    public static String raw(String key, ServerPlayer target) {
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
