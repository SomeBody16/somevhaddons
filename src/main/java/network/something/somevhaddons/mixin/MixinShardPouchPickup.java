package network.something.somevhaddons.mixin;

import iskallia.vault.container.inventory.ShardPouchContainer;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.InventorySnapshotData;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import network.something.somevhaddons.addon.curios_shard_pouch.CuriosShardPouch;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin({Inventory.class})
public class MixinShardPouchPickup implements InventorySnapshotData.InventoryAccessor {

    @Shadow
    @Final
    public Player player;

    @Shadow
    @Final
    private List<NonNullList<ItemStack>> compartments;

    public MixinShardPouchPickup() {
        super();
    }

    @Inject(
            method = {"add(Lnet/minecraft/world/item/ItemStack;)Z"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void interceptItemAddition(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() == ModItems.SOUL_SHARD) {
            if (!(this.player.containerMenu instanceof ShardPouchContainer)) {
                ItemStack pouchStack = CuriosShardPouch.getEquipped(this.player);

                if (pouchStack.is(ModItems.SHARD_POUCH)) {
                    pouchStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
                        handler.insertItem(0, stack, false);
                        stack.shrink(stack.getCount());
                        if (stack.isEmpty()) {
                            cir.setReturnValue(true);
                        }

                    });
                }
            }
        }
    }

    @Override
    public int getSize() {
        return this.compartments.stream().mapToInt(NonNullList::size).sum();
    }
}
