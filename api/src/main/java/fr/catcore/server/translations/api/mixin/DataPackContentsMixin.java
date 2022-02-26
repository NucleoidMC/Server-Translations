package fr.catcore.server.translations.api.mixin;

import fr.catcore.server.translations.api.ServerTranslations;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(DataPackContents.class)
public class DataPackContentsMixin {

    @Inject(method = "getContents", at = @At("RETURN"), cancellable = true)
    private void addServerLanguageManager(CallbackInfoReturnable<List<ResourceReloader>> cir) {
        List<ResourceReloader> immutableList = cir.getReturnValue();
        List<ResourceReloader> list = new ArrayList<>(immutableList);
        list.add(ServerTranslations.INSTANCE);
        cir.setReturnValue(list);
    }
}
