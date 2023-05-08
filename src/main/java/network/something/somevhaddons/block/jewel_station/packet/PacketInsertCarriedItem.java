package network.something.somevhaddons.block.jewel_station.packet;

import iskallia.vault.init.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import network.something.somevhaddons.block.jewel_station.gui.JewelStationMenu;

import java.util.function.Supplier;

public class PacketInsertCarriedItem {

    protected final boolean toCalculator;

    public PacketInsertCarriedItem(boolean toCalculator) {
        this.toCalculator = toCalculator;
    }

    public PacketInsertCarriedItem(FriendlyByteBuf buf) {
        this.toCalculator = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(toCalculator);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        var ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null
                    && player.containerMenu instanceof JewelStationMenu menu
                    && menu.getCarried().is(ModItems.JEWEL)
            ) {
                var item = menu.getCarried();
                var handler = toCalculator
                        ? menu.jewelStation.calculatorHandler
                        : menu.jewelStation.chestHandler;

                handler.addItem(item);
                player.containerMenu.setCarried(ItemStack.EMPTY);
            }
        });
        return true;
    }

}
