package xyz.nucleoid.server.translations.api;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.LanguageGetter;
import xyz.nucleoid.server.translations.impl.ServerTranslations;
import org.jetbrains.annotations.Nullable;

public interface LocalizationTarget {
    PacketContext.Key<String> LANGUAGE = PacketContext.key(Identifier.fromNamespaceAndPath("server_translations_api", "lang"));

    @Nullable
    static LocalizationTarget forPacket() {
        PacketContext context = PacketContext.get();
        if (context != null) {
            String lang = context.get(LANGUAGE);
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

    static LocalizationTarget of(ServerGamePacketListenerImpl connection) {
        return of(connection.getPlayer());
    }

    static LocalizationTarget ofSystem() {
        return ServerTranslations.INSTANCE.systemTarget;
    }
    @Deprecated
    static LocalizationTarget of() {
        return ofSystem();
    }
}
