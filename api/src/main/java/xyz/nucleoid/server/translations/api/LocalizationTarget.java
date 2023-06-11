package xyz.nucleoid.server.translations.api;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.ServerTranslations;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public interface LocalizationTarget {
    @Nullable
    static LocalizationTarget forPacket() {
        PacketContext context = PacketContext.get();
        return (LocalizationTarget) context.getTarget();
    }

    @Nullable
    String getLanguageCode();

    default ServerLanguage getLanguage() {
        return ServerTranslations.INSTANCE.getLanguage(this);
    }

    static LocalizationTarget of(ServerPlayerEntity player) {
        return (LocalizationTarget) player;
    }

    static LocalizationTarget of(ServerPlayNetworkHandler handler) {
        return (LocalizationTarget) handler;
    }

    static LocalizationTarget of() {
        return ServerTranslations.INSTANCE.systemTarget;
    }
}
