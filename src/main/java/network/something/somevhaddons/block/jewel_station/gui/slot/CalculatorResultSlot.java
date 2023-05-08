package network.something.somevhaddons.block.jewel_station.gui.slot;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class CalculatorResultSlot extends Slot {

    private static final Container emptyInventory = new SimpleContainer(0);


    public CalculatorResultSlot(int pX, int pY) {
        super(emptyInventory, 0, pX, pY);
    }

    @Override
    public void onQuickCraft(ItemStack pOldStack, ItemStack pNewStack) {
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player pPlayer) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    @Nonnull
    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }
}
