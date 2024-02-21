package network.something.somevhaddons.mixin;

import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemShardPouch;
import net.minecraft.world.entity.player.Inventory;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.addon.curios_shard_pouch.CuriosShardPouch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemShardPouch.class)
public class ItemShardPouchMixin {

    @Inject(
            method = "getShardCount(Lnet/minecraft/world/entity/player/Inventory;)I",
            at = @At("RETURN"),
            cancellable = true,
            remap = false,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void getShardCount(Inventory playerInventory, CallbackInfoReturnable<Integer> cir,
                                      int shards) {

        var curioPouch = CuriosShardPouch.getEquipped(playerInventory.player);
        SomeVHAddons.LOGGER.info("Curio pouch: " + curioPouch);
        if (curioPouch.is(ModItems.SHARD_POUCH)) {
            SomeVHAddons.LOGGER.info("Curio pouch count: " + ItemShardPouch.getContainedStack(curioPouch).getCount());
            shards += ItemShardPouch.getContainedStack(curioPouch).getCount();
        }

        SomeVHAddons.LOGGER.info("Shards: " + shards);
        cir.setReturnValue(shards);
    }

    @Inject(
            method = "reduceShardAmount",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void reduceShardAmount(Inventory playerInventory, int count, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        var player = playerInventory.player;

        // Inventory
        for (int slot = 0; slot < playerInventory.getContainerSize(); ++slot) {
            var stack = playerInventory.getItem(slot);
            if (stack.is(ModItems.SHARD_POUCH)) {
                var shardStack = ItemShardPouch.getContainedStack(stack);
                var toReduce = Math.min(count, shardStack.getCount());
                if (!simulate) {
                    shardStack.setCount(shardStack.getCount() - toReduce);
                    ItemShardPouch.setContainedStack(stack, shardStack);
                }

                count -= toReduce;
            } else if (stack.is(ModItems.SOUL_SHARD)) {
                int toReduce = Math.min(count, stack.getCount());
                if (!simulate) {
                    stack.shrink(toReduce);
                    playerInventory.setItem(slot, stack);
                }

                count -= toReduce;
            }

            if (count <= 0) {
                cir.setReturnValue(true);
                return;
            }
        }

        // Curios
        var curioPouch = CuriosShardPouch.getEquipped(player);
        if (curioPouch.is(ModItems.SHARD_POUCH)) {
            var shardStack = ItemShardPouch.getContainedStack(curioPouch);
            var toReduce = Math.min(count, shardStack.getCount());
            if (!simulate) {
                shardStack.setCount(shardStack.getCount() - toReduce);
                ItemShardPouch.setContainedStack(curioPouch, shardStack);
            }

            count -= toReduce;
            if (count <= 0) {
                cir.setReturnValue(true);
                return;
            }
        }

        cir.setReturnValue(false);
    }

}
