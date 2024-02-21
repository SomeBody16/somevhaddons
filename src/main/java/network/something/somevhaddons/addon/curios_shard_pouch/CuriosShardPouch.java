package network.something.somevhaddons.addon.curios_shard_pouch;

import iskallia.vault.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import network.something.somevhaddons.SomeVHAddons;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod.EventBusSubscriber(modid = SomeVHAddons.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CuriosShardPouch {

    public static final String ID = "shard_pouch";
    public static final ResourceLocation ICON = new ResourceLocation(SomeVHAddons.ID,
            "item/empty_shard_pouch_slot");

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
                .findFirstCurio(player, ModItems.SHARD_POUCH)
                .isPresent();
    }

    public static ItemStack getEquipped(LivingEntity player) {
        return CuriosApi.getCuriosHelper()
                .findFirstCurio(player, ModItems.SHARD_POUCH)
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }
}
