package network.something.somevhaddons.block.jewel_station;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.RegistryObject;
import network.something.somevhaddons.block.jewel_station.gui.JewelStationMenu;
import network.something.somevhaddons.block.jewel_station.inventory.JewelStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nonnull;

public class JewelStationBlockEntity extends BlockEntity implements IAnimatable, MenuProvider {
    public static final String ID = "jewel_station";
    public static RegistryObject<BlockEntityType<JewelStationBlockEntity>> TYPE;

    protected AnimationFactory factory = GeckoLibUtil.createFactory(this);

    protected LazyOptional<IItemHandler> lazyChestHandler = LazyOptional.empty();
    public final JewelStackHandler chestHandler = new JewelStackHandler() {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            setChanged();
        }
    };

    protected LazyOptional<IItemHandler> lazyCalculatorHandler = LazyOptional.empty();
    public final JewelStackHandler calculatorHandler = new JewelStackHandler() {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            setChanged();
        }
    };

    public JewelStationBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TYPE.get(), pPos, pBlockState);
    }


    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(
                new AnimationController<>(this, "controller",
                        0, this::predicate));
    }

    protected <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(
                new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP)
        );
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("block.somevhaddons.jewel_station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new JewelStationMenu(pContainerId, pPlayerInventory, this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyChestHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyChestHandler = LazyOptional.of(() -> chestHandler);
        lazyCalculatorHandler = LazyOptional.of(() -> calculatorHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyChestHandler.invalidate();
        lazyCalculatorHandler.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("chest", chestHandler.serializeNBT());
        tag.put("calculator", calculatorHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        chestHandler.deserializeNBT(nbt.getCompound("chest"));
        calculatorHandler.deserializeNBT(nbt.getCompound("calculator"));
    }

    public void drops() {
        drop(chestHandler);
        drop(calculatorHandler);
    }

    private void drop(IItemHandler handler) {
        SimpleContainer inventory = new SimpleContainer(handler.getSlots());
        for (int i = 0; i < handler.getSlots(); i++) {
            inventory.setItem(i, handler.getStackInSlot(i));
        }
        Containers.dropContents(level, this.worldPosition, inventory);
    }
}
