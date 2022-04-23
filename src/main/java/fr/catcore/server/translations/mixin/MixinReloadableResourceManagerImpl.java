package fr.catcore.server.translations.mixin;

import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ReloadableResourceManagerImpl.class)
public class MixinReloadableResourceManagerImpl {

    @ModifyArg(method = "reload", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0, remap = false), index = 0)
    private String translated_loading(String string, Object args) {
        return Text.translatable("text.translated_server.loading.datapacks", args).getString();
    }

}
