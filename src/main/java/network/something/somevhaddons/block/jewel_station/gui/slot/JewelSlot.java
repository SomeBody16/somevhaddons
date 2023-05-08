package network.something.somevhaddons.block.jewel_station.gui.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import network.something.somevhaddons.block.jewel_station.util.JewelStackHandler;

public class JewelSlot extends SlotItemHandler {

    private final JewelStackHandler itemHandler;

    public JewelSlot(JewelStackHandler itemHandler, int index, int pX, int pY) {
        super(itemHandler, index, pX, pY);
        this.itemHandler = itemHandler;
    }

    @Override
    public void set(ItemStack pStack) {
        itemHandler.insertItem(pStack, false);
        setChanged();
    }

    @Override
    public int getMaxStackSize(ItemStack pStack) {
        return getMaxStackSize();
    }
}
