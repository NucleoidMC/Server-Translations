package fr.catcore.server.translations.api.resource.language;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.util.Language;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerLanguage extends Language {
    private static final Pattern PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z])");
    private final ServerLanguageDefinition definition;
    private final LanguageMap map;

    public ServerLanguage(ServerLanguageDefinition definition, LanguageMap map) {
        this.definition = definition;
        this.map = map;
    }

    public ServerLanguage(ServerLanguageDefinition definition) {
        this(definition, new LanguageMap());
    }

    public ServerLanguageDefinition getDefinition() {
        return this.definition;
    }

    @Override
    public String get(String key) {
        String value = this.map.get(key);
        if (value == null) {
            ServerLanguage system = ServerLanguageManager.INSTANCE.getSystemLanguage();
            value = system.map.get(key);
        }

        return value != null ? value : key;
    }

    @Override
    public boolean hasTranslation(String key) {
        return this.map.contains(key);
    }

    @Override
    public boolean isRightToLeft() {
        return this.definition.isRightToLeft();
    }

    public String reorder(String string, boolean allowTokens) {
        if (!this.definition.isRightToLeft()) {
            return string;
        } else {
            if (allowTokens && string.indexOf('%') != -1) {
                string = method_29389(string);
            }

            return this.reorder(string);
        }
    }

    public static String method_29389(String string) {
        Matcher matcher = PATTERN.matcher(string);
        StringBuffer buffer = new StringBuffer();
        int var3 = 1;

        while (matcher.find()) {
            String string2 = matcher.group(1);
            String string3 = string2 != null ? string2 : Integer.toString(var3++);
            String string4 = matcher.group(2);
            String string5 = Matcher.quoteReplacement("\u2066%" + string3 + "$" + string4 + "\u2069");
            matcher.appendReplacement(buffer, string5);
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String reorder(String string) {
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(string), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(10);
        } catch (ArabicShapingException e) {
            return string;
        }
    }

    public void putAll(LanguageMap map) {
        this.map.putAll(map);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public OrderedText reorder(StringVisitable text) {
        return visitor -> {
            return text.visit((style, string) -> {
                return TextVisitFactory.visitFormatted(string, style, visitor) ? Optional.empty() : StringVisitable.TERMINATE_VISIT;
            }, Style.EMPTY).isPresent();
        };
    }
}
