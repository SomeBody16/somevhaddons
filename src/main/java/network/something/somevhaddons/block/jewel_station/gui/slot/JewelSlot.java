package network.something.somevhaddons.block.jewel_station.gui.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import network.something.somevhaddons.block.jewel_station.inventory.JewelStackHandler;

public class JewelSlot extends SlotItemHandler {

    public JewelSlot(JewelStackHandler itemHandler, int index, int pX, int pY) {
        super(itemHandler, index, pX, pY);
    }

    @Override
    public int getMaxStackSize(ItemStack pStack) {
        return 1;
    }
}
