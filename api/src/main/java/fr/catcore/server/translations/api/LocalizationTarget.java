package fr.catcore.server.translations.api;

import fr.catcore.server.translations.api.resource.language.ServerLanguage;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public interface LocalizationTarget {
    @Nullable
    static LocalizationTarget forPacket() {
        PacketContext context = PacketContext.get();
        return (LocalizationTarget) context.getTarget();
    }

    String getLanguageCode();

    default ServerLanguage getLanguage() {
        return ServerTranslations.INSTANCE.getLanguage(this);
    }
}
