package fr.catcore.server.translations.api.resource.language;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TranslationAccess {
    TranslationAccess EMPTY = new TranslationAccess() {
        @Override
        @Nullable
        public String getOrNull(String key) {
            return null;
        }

        @Override
        public boolean contains(String key) {
            return false;
        }
    };

    @NotNull
    default String get(String key) {
        String translation = this.getOrNull(key);
        return translation != null ? translation : key;
    }

    @Nullable
    String getOrNull(String key);

    boolean contains(String key);

    default TranslationAccess union(TranslationAccess right) {
        if (this == right) {
            return this;
        }
        return new Union(this, right);
    }

    default TranslationAccess subtract(TranslationAccess right) {
        if (this == right) {
            return EMPTY;
        }
        return new Subtract(this, right);
    }

    final class Union implements TranslationAccess {
        private final TranslationAccess left;
        private final TranslationAccess right;

        public Union(TranslationAccess left, TranslationAccess right) {
            this.left = left;
            this.right = right;
        }

        @Override
        @Nullable
        public String getOrNull(String key) {
            String left = this.left.getOrNull(key);
            return left != null ? left : this.right.getOrNull(key);
        }

        @Override
        public boolean contains(String key) {
            return this.left.contains(key) || this.right.contains(key);
        }
    }

    final class Subtract implements TranslationAccess {
        private final TranslationAccess left;
        private final TranslationAccess right;

        public Subtract(TranslationAccess left, TranslationAccess right) {
            this.left = left;
            this.right = right;
        }

        @Override
        @Nullable
        public String getOrNull(String key) {
            if (this.right.contains(key)) {
                return null;
            }
            return this.left.getOrNull(key);
        }

        @Override
        public boolean contains(String key) {
            return !this.right.contains(key) && this.left.contains(key);
        }
    }
}
