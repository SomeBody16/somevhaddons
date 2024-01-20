package network.something.somevhaddons.addon.curios_junk;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import network.something.somevhaddons.SomeVHAddons;

@Mod.EventBusSubscriber(modid = SomeVHAddons.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CuriosJunkTexture {

    @SubscribeEvent
    public static void textureStitchEvent(final TextureStitchEvent.Pre event) {
        event.addSprite(CuriosJunk.ICON);
    }
}
