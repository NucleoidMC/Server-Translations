package xyz.nucleoid.server.translations.api;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.fabricmc.fabric.api.networking.v1.context.PacketContextProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jspecify.annotations.Nullable;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.LanguageGetter;
import xyz.nucleoid.server.translations.impl.ServerTranslations;

public interface LocalizationTarget {
    @Nullable
    static LocalizationTarget forPacket() {
        PacketContext context = PacketContext.get();
        if (context != null) {
            String lang = context.get(ServerTranslations.LANGUAGE_KEY);
            if (lang != null) {
                return () -> lang;
            }
        }
        return null;
    }

    @Nullable
    String getLanguageCode();

    default ServerLanguage getLanguage() {
        return ServerTranslations.INSTANCE.getLanguage(this);
    }

    static LocalizationTarget of(ServerPlayer player) {
        return ((LanguageGetter) player)::stapi$getLanguage;
    }

    static LocalizationTarget of(PacketContext context) {
        return () -> context.orElse(ServerTranslations.LANGUAGE_KEY, "en_us");
    }

    static LocalizationTarget of(PacketContextProvider provider) {
        return of(provider.getPacketContext());
    }

    static LocalizationTarget ofSystem() {
        return ServerTranslations.INSTANCE.systemTarget;
    }
}
