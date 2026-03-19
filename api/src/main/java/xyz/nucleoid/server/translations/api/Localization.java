package xyz.nucleoid.server.translations.api;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.fabricmc.fabric.api.networking.v1.context.PacketContextProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.LocalizableComponent;

public final class Localization {
    private Localization() {}

    public static Component component(Component text, ServerPlayer target) {
        return component(text, LocalizationTarget.of(target));
    }

    public static Component component(Component text, PacketContextProvider target) {
        return component(text, LocalizationTarget.of(target));
    }

    public static Component component(Component text, PacketContext target) {
        return component(text, LocalizationTarget.of(target));
    }

    public static Component component(Component text, LocalizationTarget target) {
        return component(text, target.getLanguage());
    }

    public static Component component(Component text, ServerLanguage language) {
        return LocalizableComponent.asLocalizedFor(text, language);
    }

    @Nullable
    public static String raw(String key, ServerPlayer target) {
        return raw(key, LocalizationTarget.of(target));
    }

    @Nullable
    public static String raw(String key, PacketContextProvider target) {
        return raw(key, LocalizationTarget.of(target));
    }

    @Nullable
    public static String raw(String key, PacketContext target) {
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
