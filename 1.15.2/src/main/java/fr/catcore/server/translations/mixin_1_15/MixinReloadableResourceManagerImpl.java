package fr.catcore.server.translations.mixin_1_15;

import net.minecraft.resource.ReloadableResourceManagerImpl;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ReloadableResourceManagerImpl.class)
public class MixinReloadableResourceManagerImpl {

    // TODO: find a way to fix this mixin.
//    @ModifyArg(method = "beginMonitoredReload", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"), index = 0)
//    private String translated_loading(String string, Object p0) {
//        System.out.println(p0);
//        return new TranslatableText("text.translated_server.loading.datapacks", p0.toString()).getString();
//    }
}
