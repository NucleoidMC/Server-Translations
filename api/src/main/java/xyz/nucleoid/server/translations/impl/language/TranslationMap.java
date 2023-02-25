package xyz.nucleoid.server.translations.impl.language;

import xyz.nucleoid.server.translations.api.language.TranslationAccess;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public final class TranslationMap implements TranslationAccess {
    private final Map<String, String> map = new Object2ObjectOpenHashMap<>();

    public TranslationMap() {
    }

    public TranslationMap(TranslationMap map) {
        this.map.putAll(map.map);
    }

    public void put(String key, String translation) {
        this.map.put(key, translation);
    }

    public void putAll(TranslationMap map) {
        for (Map.Entry<String, String> entry : map.map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    @NotNull
    public String get(String key) {
        return this.map.getOrDefault(key, key);
    }

    @Override
    @Nullable
    public String getOrNull(String key) {
        return this.map.get(key);
    }

    @Override
    public boolean contains(String key) {
        return this.map.containsKey(key);
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return this.map.entrySet();
    }

    private void clear() {
        this.map.clear();
    }

    public int size() {
        return this.map.size();
    }
}
