package network.something.somevhaddons.block.jewel_station.gui.slot;

import iskallia.vault.init.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import network.something.somevhaddons.block.jewel_station.util.JewelStackHandler;

public class JewelSlot extends SlotItemHandler {

    public JewelSlot(JewelStackHandler itemHandler, int pSlot, int pX, int pY) {
        super(itemHandler, pSlot, pX, pY);
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        return pStack.is(ModItems.JEWEL);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
