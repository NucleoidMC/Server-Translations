package fr.catcore.server.translations.api.mixin;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(BossBarS2CPacket.class)
public interface BossBarS2CPacketAccessor {
    @Accessor("uuid")
    UUID getUUID();

    @Accessor("type")
    BossBarS2CPacket.Type getBarType();

    @Accessor("name")
    Text getMessage();

    @Accessor("percent")
    float getBarPercent();

    @Accessor("color")
    BossBar.Color getBarColor();

    @Accessor("overlay")
    BossBar.Style getBarOverlay();

    @Accessor("darkenSky")
    boolean barShouldDarkenSky();

    @Accessor("dragonMusic")
    boolean barHasDragonMusic();

    @Accessor("thickenFog")
    boolean barShouldThickenFog();

    @Accessor("uuid")
    void setUUID(UUID uuid);

    @Accessor("type")
    void setBarType(BossBarS2CPacket.Type type);

    @Accessor("name")
    void setMessage(Text message);

    @Accessor("percent")
    void setBarPercent(float percent);

    @Accessor("color")
    void setBarColor(BossBar.Color color);

    @Accessor("overlay")
    void setBarOverlay(BossBar.Style style);

    @Accessor("darkenSky")
    void setBarShouldDarkenSky(boolean darkenSky);

    @Accessor("dragonMusic")
    void setBarHasDragonMusic(boolean hasDragonMusic);

    @Accessor("thickenFog")
    void setBarShouldThickenFog(boolean thickenFog);
}
