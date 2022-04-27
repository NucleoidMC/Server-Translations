package fr.catcore.server.translations.mixin;

import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.util.Supplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ReloadableResourceManagerImpl.class)
public class MixinReloadableResourceManagerImpl {

//    @ModifyArgs(method = "reload", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", ordinal = 0, remap = false))
//    private void translated_loading(Args args) {
//        StringBuilder str = new StringBuilder();
//        boolean bol = false;
//        for (Supplier<?> supplier : (Supplier<?>[]) args.get(1)) {
//            if (bol) str.append(",").append(supplier.get());
//            else {
//                bol = true;
//                str = new StringBuilder(supplier.get().toString());
//            }
//        }
//        args.set(0, new TranslatableText("text.translated_server.loading.datapacks", str.toString()).getString());
//    }
}
