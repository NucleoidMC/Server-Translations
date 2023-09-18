package xyz.nucleoid.server.translations.api;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.LanguageGetter;
import xyz.nucleoid.server.translations.impl.ServerTranslations;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public interface LocalizationTarget {
    @Nullable
    static LocalizationTarget forPacket() {
        PacketContext context = PacketContext.get();
        if (context != null) {
            var options = context.getClientOptions();
            if (options != null) {
                return options::language;
            }
        }
        return null;
    }

    @Nullable
    String getLanguageCode();

    default ServerLanguage getLanguage() {
        return ServerTranslations.INSTANCE.getLanguage(this);
    }

    static LocalizationTarget of(ServerPlayerEntity player) {
        return ((LanguageGetter) player)::stapi$getLanguage;
    }

    static LocalizationTarget of(ServerPlayNetworkHandler handler) {
        return of(handler.getPlayer());
    }

    static LocalizationTarget ofSystem() {
        return ServerTranslations.INSTANCE.systemTarget;
    }
    @Deprecated
    static LocalizationTarget of() {
        return ofSystem();
    }
}
