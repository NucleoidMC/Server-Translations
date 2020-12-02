package fr.catcore.server.translations.api.resource.language;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class LanguageMap {
    private final Map<String, String> map = new HashMap<>();

    public LanguageMap() {
    }

    public LanguageMap(LanguageMap map) {
        this.map.putAll(map.map);
    }

    public void put(String key, String translation) {
        this.map.put(key, translation);
    }

    public void putAll(LanguageMap map) {
        for (Map.Entry<String, String> entry : map.map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public String get(String key) {
        return this.map.getOrDefault(key, key);
    }

    @Nullable
    public String getOrNull(String key) {
        return this.map.get(key);
    }

    public boolean contains(String key) {
        return this.map.containsKey(key);
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return this.map.entrySet();
    }

    protected void clear() {
        this.map.clear();
    }
}
