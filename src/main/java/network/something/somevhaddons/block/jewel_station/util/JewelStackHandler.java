package network.something.somevhaddons.block.jewel_station.util;

import iskallia.vault.init.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class JewelStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundTag> {
    protected NonNullList<ItemStack> stacks;

    public JewelStackHandler() {
        stacks = NonNullList.create();
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
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        stacks = NonNullList.create();
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            stacks.add(ItemStack.of(itemTags));
        }
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        stacks.add(stack);
        onContentsChanged();
    }

    @Override
    public int getSlots() {
        return stacks.size();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= stacks.size()
                ? ItemStack.EMPTY
                : stacks.get(slot);
    }

    public int getRows(int columns) {
        return (int) Math.ceil(stacks.size() / (double) columns);
    }

    public ItemStack insertItem(@NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!simulate) {
            stacks.add(stack);
            onContentsChanged();
        }
        return stack;
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return insertItem(stack, simulate);
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) return ItemStack.EMPTY;

        ItemStack existing = getStackInSlot(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;

        if (!simulate) {
            this.stacks.remove(slot);
            onContentsChanged();
            return existing;
        }
        return existing.copy();
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.is(ModItems.JEWEL);
    }

    protected void onContentsChanged() {
    }
}
