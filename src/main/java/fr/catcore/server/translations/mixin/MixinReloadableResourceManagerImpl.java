package fr.catcore.server.translations.mixin;

import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.util.Supplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ReloadableResourceManagerImpl.class)
public class MixinReloadableResourceManagerImpl {

    @ModifyArg(method = "beginMonitoredReload", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;[Lorg/apache/logging/log4j/util/Supplier;)V", ordinal = 0), index = 0)
    private String translated_loading(String string, Supplier... p0) {
        String str = "";
        boolean bol = false;
        for (Supplier supplier : p0) {
            if (bol) str = str + "," + supplier.get();
            else {
                bol = true;
                str = supplier.get().toString();
            }
        }
        return new TranslatableText("text.translated_server.loading.datapacks", str).getString();
    }
}
