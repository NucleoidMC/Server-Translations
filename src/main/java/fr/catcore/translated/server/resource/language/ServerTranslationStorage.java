package fr.catcore.translated.server.resource.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerTranslationStorage extends Language {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern field_25288 = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z])");
    private final Map<String, String> translations;
    private final boolean rightToLeft;

    public ServerTranslationStorage(Map<String, String> translations, boolean rightToLeft) {
        this.translations = translations;
        this.rightToLeft = rightToLeft;
    }

//    public static ServerTranslationStorage load(ResourceManager resourceManager, List<ServerLanguageDefinition> definitions) {
//        Map<String, String> map = Maps.newHashMap();
//        boolean bl = false;
//        Iterator var4 = definitions.iterator();
//
//        while(var4.hasNext()) {
//            ServerLanguageDefinition languageDefinition = (ServerLanguageDefinition)var4.next();
//            bl |= languageDefinition.isRightToLeft();
//            String string = String.format("lang/%s.json", languageDefinition.getCode());
//            Iterator var7 = resourceManager.getAllNamespaces().iterator();
//
//            while(var7.hasNext()) {
//                String string2 = (String)var7.next();
//
//                try {
//                    Identifier identifier = new Identifier(string2, string);
//                    load((List)resourceManager.getAllResources(identifier), (Map)map);
//                } catch (FileNotFoundException var10) {
//                } catch (Exception var11) {
//                    LOGGER.warn("Skipped language file: {}:{} ({})", string2, string, var11.toString());
//                }
//            }
//        }
//
//        return new ServerTranslationStorage(ImmutableMap.copyOf(map), bl);
//    }

//    private static void load(List<Resource> resources, Map<String, String> translationMap) {
//        Iterator var2 = resources.iterator();
//
//        while(var2.hasNext()) {
//            Resource resource = (Resource)var2.next();
//
//            try {
//                InputStream inputStream = resource.getInputStream();
//                Throwable var5 = null;
//
//                try {
//                    Language.load(inputStream, translationMap::put);
//                } catch (Throwable var15) {
//                    var5 = var15;
//                    throw var15;
//                } finally {
//                    if (inputStream != null) {
//                        if (var5 != null) {
//                            try {
//                                inputStream.close();
//                            } catch (Throwable var14) {
//                                var5.addSuppressed(var14);
//                            }
//                        } else {
//                            inputStream.close();
//                        }
//                    }
//
//                }
//            } catch (IOException var17) {
//                LOGGER.warn("Failed to load translations from {}", resource, var17);
//            }
//        }
//
//    }

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
        Matcher matcher = field_25288.matcher(string);
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
}
