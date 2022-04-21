package fr.catcore.server.translations.api.mixin.packet;

import fr.catcore.server.translations.api.LocalizationTarget;
import fr.catcore.server.translations.api.text.LocalizableMutableText;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BlockEntityUpdateS2CPacket.class)
public class BlockEntityUpdateS2CPacketMixin {

    @Shadow @Final private BlockEntityType<?> blockEntityType;

    @ModifyArg(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/network/PacketByteBuf;"))
    private NbtCompound translateNbt(NbtCompound nbtCompound) {
        var target = LocalizationTarget.forPacket();

        if (this.blockEntityType == BlockEntityType.SIGN && target != null) {
            var nbt = nbtCompound.copy();
            nbt.putString("Text1", this.parseText(nbt.getString("Text1"), target));
            nbt.putString("Text2", this.parseText(nbt.getString("Text2"), target));
            nbt.putString("Text3", this.parseText(nbt.getString("Text3"), target));
            nbt.putString("Text4", this.parseText(nbt.getString("Text4"), target));
            return nbt;
        }
        return nbtCompound;
    }

    @Unique
    private String parseText(String text, LocalizationTarget target) {
        var parsed = (LocalizableMutableText) Text.Serializer.fromLenientJson(text);

        if (parsed != null && parsed.shouldLocalize()) {
            return Text.Serializer.toJson(parsed.asLocalizedFor(target));
        } else {
            return text;
        }
    }
}
