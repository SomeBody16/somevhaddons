package network.something.somevhaddons.mixin;

import iskallia.vault.block.entity.VaultArtisanStationTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import network.something.somevhaddons.addon.artisan_station.ArtisanStationItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(VaultArtisanStationTileEntity.class)
public abstract class ArtisanStationMixin extends BlockEntity implements MenuProvider {

    @Unique
    protected LazyOptional<IItemHandler> somevhaddons$lazyItemHandler = LazyOptional.empty();

    public ArtisanStationMixin(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return somevhaddons$lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        var station = (VaultArtisanStationTileEntity) (Object) this;
        var handler = new ArtisanStationItemHandler(station);
        somevhaddons$lazyItemHandler = LazyOptional.of(() -> handler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        somevhaddons$lazyItemHandler.invalidate();
    }
}
