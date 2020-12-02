package fr.catcore.server.translations.api;

import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public interface LocalizationTarget {
    @Nullable
    static LocalizationTarget forPacket() {
        PacketContext context = PacketContext.get();
        return (LocalizationTarget) context.getTarget();
    }

    String getLanguageCode();

    default Language getLanguage() {
        return ServerTranslations.INSTANCE.getLanguage(this);
    }
}
