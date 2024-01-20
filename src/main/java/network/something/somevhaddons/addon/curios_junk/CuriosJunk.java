package network.something.somevhaddons.addon.curios_junk;

import iskallia.vault.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import network.something.somevhaddons.SomeVHAddons;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod.EventBusSubscriber(modid = SomeVHAddons.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CuriosJunk {

    public static final String ID = "junk_identifier";
    public static final ResourceLocation ICON = new ResourceLocation(SomeVHAddons.ID,
            "item/empty_junk_identifier_slot");

    @SubscribeEvent
    public static void interModEnqueueEvent(final InterModEnqueueEvent event) {
        InterModComms.sendTo(
                CuriosApi.MODID,
                SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder(ID)
                        .icon(ICON)
                        .build()
        );
    }

    public static boolean hasEquipped(ServerPlayer player) {
        return CuriosApi.getCuriosHelper()
                .findFirstCurio(player, ModItems.VAULT_CHARM)
                .isPresent();
    }

}
