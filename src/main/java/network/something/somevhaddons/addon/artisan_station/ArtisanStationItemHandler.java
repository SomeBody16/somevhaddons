package network.something.somevhaddons.addon.artisan_station;

import iskallia.vault.block.entity.VaultArtisanStationTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArtisanStationItemHandler extends ItemStackHandler {

    public static final List<ItemLike> WHITELIST = List.of(
            ModItems.WILD_FOCUS,
            ModItems.AMPLIFYING_FOCUS,
            ModItems.NULLIFYING_FOCUS,
            ModItems.RESILIENT_FOCUS,
            ModItems.OPPORTUNISTIC_FOCUS,
            ModItems.FUNDAMENTAL_FOCUS,
            ModItems.WANING_FOCUS,
            ModItems.WAXING_FOCUS,
            ModBlocks.VAULT_BRONZE,
            ModItems.VAULT_PLATING
    );

    protected final VaultArtisanStationTileEntity station;

    public ArtisanStationItemHandler(VaultArtisanStationTileEntity station) {
        super(12);
        this.station = station;
        for (int i = 0; i < 12; i++) {
            this.stacks.set(i, station.getInventory().getItem(i));
        }
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        if (this.isItemValid(slot, stack)) {
            return getSlotLimit(slot);
        }
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return WHITELIST.stream().anyMatch(itemLike -> stack.is(itemLike.asItem()));
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        station.setChanged();
    }
}
