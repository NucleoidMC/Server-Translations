package xyz.nucleoid.server.translations.impl.language;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public final class TranslationStore {
    private final Map<String, TranslationMap> translations = new Object2ObjectOpenHashMap<>();
    private final Multimap<String, Supplier<@Nullable TranslationMap>> translationSuppliers = HashMultimap.create();

    public void clear() {
        this.translationSuppliers.clear();
        this.translations.clear();
    }

    public void add(String code, Supplier<@Nullable TranslationMap> supplier) {
        TranslationMap translations = this.translations.get(code);
        if (translations != null) {
            TranslationMap map = supplier.get();
            if (map != null) {
                translations.putAll(map);
            }
        } else {
            this.translationSuppliers.put(code, supplier);
        }
    }

    public TranslationMap get(String code) {
        TranslationMap translations = this.translations.get(code);
        if (translations == null) {
            translations = this.tryLoad(code);
        }
        return translations;
    }

    private TranslationMap tryLoad(String code) {
        Collection<Supplier<@Nullable TranslationMap>> suppliers = this.translationSuppliers.removeAll(code);

        TranslationMap translations = new TranslationMap();
        this.translations.put(code, translations);

        for (Supplier<@Nullable TranslationMap> supplier : suppliers) {
            TranslationMap map = supplier.get();
            if (map != null) {
                translations.putAll(map);
            }
        }

        return translations;
    }
}
