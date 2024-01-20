package network.something.somevhaddons.addon.curios_junk;

import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.VaultCharmData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import network.something.somevhaddons.SomeVHAddons;

@Mod.EventBusSubscriber(modid = SomeVHAddons.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemPickupEvent {


    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onVaultCharmUse(EntityItemPickupEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            // Only in vault
            var level = player.getLevel();
            if (ServerVaults.get(level).isEmpty()) return;

            // Has charm
            if (!CuriosJunk.hasEquipped(player)) return;

            // Valid item
            var itemEntity = event.getItem();
            var stack = itemEntity.getItem();
            if (stack.isEmpty()) return;

            var whitelist = VaultCharmData.get(level).getWhitelistedItems(player);
            if (whitelist.contains(stack.getItem().getRegistryName())) {
                event.setCanceled(true);
                itemEntity.remove(Entity.RemovalReason.DISCARDED);
                level.playSound(
                        null,
                        player.getOnPos(),
                        SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS,
                        0.2f,
                        (level.random.nextFloat() - level.random.nextFloat()) * 1.4F + 2.0F
                );
            }
        }
    }
}
