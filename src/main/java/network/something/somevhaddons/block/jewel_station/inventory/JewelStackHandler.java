package network.something.somevhaddons.block.jewel_station.inventory;

import iskallia.vault.init.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.api.util.the_vault.JewelAttribute;
import network.something.somevhaddons.api.util.the_vault.JewelAttributes;
import network.something.somevhaddons.block.jewel_station.inventory.sorter.JewelAttributeSorter;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class JewelStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundTag> {

    protected ArrayList<ItemStack> stacks;
    protected JewelAttributeSorter sorter = new JewelAttributeSorter(JewelAttributes.SIZE);

    public JewelStackHandler() {
        stacks = new ArrayList<>();
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        validateSlotIndex(slot);
        this.stacks.set(slot, stack);
        onContentsChanged();
    }

    @Override
    public int getSlots() {
        return stacks.size();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return this.stacks.get(slot);
    }

    public void addItem(@Nonnull ItemStack stack) {
        stacks.add(stack);
        onContentsChanged();
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged();
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.stacks.set(slot, ItemStack.EMPTY);
                onContentsChanged();
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged();
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.is(ModItems.JEWEL);
    }

    @Override
    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                stack.save(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);

        SomeVHAddons.LOGGER.info("serializeNBT 1 : {}", stacks.size());
        SomeVHAddons.LOGGER.info("serializeNBT 2 : {}", nbtTagList.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        stacks = new ArrayList<>();
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            stacks.add(ItemStack.of(itemTags));
        }
        SomeVHAddons.LOGGER.info("deserializeNBT 1 : {}", stacks.size());
        SomeVHAddons.LOGGER.info("deserializeNBT 2 : {}", tagList.size());
        onLoad();
    }

    public int getRows(int columns) {
        SomeVHAddons.LOGGER.info(
                "getRows : [stackSize : {}]",
                stacks.size()
        );
        return (int) Math.ceil(stacks.size() / (double) columns);
    }

    protected void validateSlotIndex(int slot) {
        while (stacks.size() < slot + 1) {
            stacks.add(ItemStack.EMPTY);
        }
    }

    protected void onLoad() {

    }

    protected void onContentsChanged() {
        sort();
    }

    public void setSortAttribute(JewelAttribute attr) {
        this.sorter = new JewelAttributeSorter(attr);
    }

    public void sort() {
        stacks.removeIf(ItemStack::isEmpty);
        stacks.sort(sorter);
        SomeVHAddons.LOGGER.info("Sorted: {}", stacks.size());
    }
}
