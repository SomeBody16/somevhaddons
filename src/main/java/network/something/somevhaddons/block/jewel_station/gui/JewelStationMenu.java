package network.something.somevhaddons.block.jewel_station.gui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.RegistryObject;
import network.something.somevhaddons.api.screen.BaseMenu;
import network.something.somevhaddons.api.screen.widget.ScrollbarWidget;
import network.something.somevhaddons.api.util.MenuUtil;
import network.something.somevhaddons.block.jewel_station.JewelStationBlock;
import network.something.somevhaddons.block.jewel_station.JewelStationBlockEntity;
import network.something.somevhaddons.block.jewel_station.gui.slot.CalculatorResultSlot;
import network.something.somevhaddons.block.jewel_station.gui.slot.JewelSlot;
import org.jetbrains.annotations.NotNull;

public class JewelStationMenu extends BaseMenu {
    public static final String ID = "jewel_station";
    public static RegistryObject<MenuType<JewelStationMenu>> TYPE;

    protected final JewelStationBlockEntity jewelStation;
    protected final Level level;
    protected final Inventory playerInventory;

    public ScrollbarWidget chestScrollbar;
    public ScrollbarWidget calculatorScrollbar;

    public JewelStationMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public JewelStationMenu(int pContainerId, Inventory playerInventory, BlockEntity jewelStation) {
        super(TYPE.get(), pContainerId);

        this.jewelStation = (JewelStationBlockEntity) jewelStation;
        this.level = playerInventory.player.level;
        this.playerInventory = playerInventory;

        initSlots();
    }

    public void initSlots() {
        slots.clear();

        addChestInventory();
        addCalculatorInventory();

        addPlayerInventory();
        addPlayerHotbar();
    }

    protected void addChestInventory() {
        var visibleRows = 6;
        var visibleColumns = 4;
        var storedRows = jewelStation.chestHandler.getRows(visibleColumns);
        var scrollProgress = chestScrollbar == null ? 0 : chestScrollbar.getProgress();

        var maxOffsetRows = Math.max(0, storedRows - visibleRows);
        var offsetRows = (int) (maxOffsetRows * scrollProgress);
        offsetRows = Math.min(maxOffsetRows, offsetRows);

        for (var row = 0; row < visibleRows; row++) {
            var actualRow = row + offsetRows;

            for (var col = 0; col < visibleColumns; col++) {
                var slot = new JewelSlot(jewelStation.chestHandler,
                        (actualRow * visibleColumns) + col,
                        8 + col * 18, 18 + row * 18) {
                    @Override
                    public void setChanged() {
                        super.setChanged();
                        initSlots();
                    }
                };
                addSlot(slot);
            }
        }
    }

    protected void addCalculatorInventory() {
        var visibleRows = 6;
        var visibleColumns = 3;
        var storedRows = jewelStation.calculatorHandler.getRows(visibleColumns);
        var scrollProgress = calculatorScrollbar == null ? 0 : calculatorScrollbar.getProgress();

        var maxOffsetRows = Math.max(0, storedRows - visibleRows);
        var offsetRows = (int) (maxOffsetRows * scrollProgress);
        offsetRows = Math.min(maxOffsetRows, offsetRows);

        for (var row = 0; row < visibleRows; row++) {
            var actualRow = row + offsetRows;

            for (var col = 0; col < visibleColumns; col++) {
                var slot = new SlotItemHandler(jewelStation.calculatorHandler,
                        (actualRow * visibleColumns) + col,
                        116 + col * 18, 18 + row * 18);
                addSlot(slot);
            }
        }

        var resultSlot = new CalculatorResultSlot(179, 27);
        addSlot(resultSlot);
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, jewelStation.getBlockPos()),
                pPlayer, JewelStationBlock.TYPE.get());
    }

    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        return new MenuUtil(slots, this::moveItemStackTo, (6 * 4) + (6 * 3))
                .quickMoveStack(pPlayer, pIndex);
    }

    protected void addPlayerInventory() {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }
    }

    protected void addPlayerHotbar() {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 198));
        }
    }
}
