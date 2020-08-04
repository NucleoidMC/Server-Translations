package fr.catcore.translated.server.resource.language;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerTranslationStorage extends Language {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z])");
    private final Map<String, String> translations;
    private final boolean rightToLeft;

    public ServerTranslationStorage(Map<String, String> translations, boolean rightToLeft) {
        this.translations = translations;
        this.rightToLeft = rightToLeft;
    }

    public String get(String key) {
        return (String)this.translations.getOrDefault(key, key);
    }

    public boolean hasTranslation(String key) {
        return this.translations.containsKey(key);
    }

    public boolean isRightToLeft() {
        return this.rightToLeft;
    }

    public String reorder(String string, boolean allowTokens) {
        if (!this.rightToLeft) {
            return string;
        } else {
            if (allowTokens && string.indexOf(37) != -1) {
                string = method_29389(string);
            }

            return this.reorder(string);
        }
    }

    public static String method_29389(String string) {
        Matcher matcher = PATTERN.matcher(string);
        StringBuffer stringBuffer = new StringBuffer();
        int var3 = 1;

        while(matcher.find()) {
            String string2 = matcher.group(1);
            String string3 = string2 != null ? string2 : Integer.toString(var3++);
            String string4 = matcher.group(2);
            String string5 = Matcher.quoteReplacement("\u2066%" + string3 + "$" + string4 + "\u2069");
            matcher.appendReplacement(stringBuffer, string5);
        }

        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private String reorder(String string) {
        try {
            Bidi bidi = new Bidi((new ArabicShaping(8)).shape(string), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(10);
        } catch (ArabicShapingException var3) {
            return string;
        }
    }

    public void addTranslation(String key, String value) {
        this.translations.putIfAbsent(key, value);
    }
}
