package xyz.nucleoid.server.translations.impl;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import static xyz.nucleoid.server.translations.impl.ServerTranslations.id;

public class LanguageContext {
    public static final PacketContext.Key<String> LANGUAGE_KEY = PacketContext.key(id("lang"));
}
