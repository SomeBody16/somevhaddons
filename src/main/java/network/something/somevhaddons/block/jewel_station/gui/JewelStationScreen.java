package network.something.somevhaddons.block.jewel_station.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.init.ModItems;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.api.screen.BaseScreen;
import network.something.somevhaddons.api.screen.widget.ScrollbarWidget;
import network.something.somevhaddons.api.util.RenderUtils;
import network.something.somevhaddons.block.jewel_station.packet.PacketInsertCarriedItem;
import network.something.somevhaddons.init.ModPackets;

@Mod.EventBusSubscriber(modid = SomeVHAddons.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JewelStationScreen extends BaseScreen<JewelStationMenu> {

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        MenuScreens.register(JewelStationMenu.TYPE.get(), JewelStationScreen::new);
    }

    public JewelStationScreen(JewelStationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        imageWidth = 214;
        imageHeight = 222;
        inventoryLabelY = imageHeight - 94;

        menu.chestScrollbar = new ScrollbarWidget(this, 81, 18, 106);
        menu.chestScrollbar.addListener((progress) -> menu.initSlots());
        menu.widgets.add(menu.chestScrollbar);

        menu.calculatorScrollbar = new ScrollbarWidget(this, 101, 18, 106);
        menu.calculatorScrollbar.addListener((progress) -> menu.initSlots());
        menu.widgets.add(menu.calculatorScrollbar);
    }

    @Override
    protected void renderBackground(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        bindTexture(SomeVHAddons.ID, "textures/gui/jewel_station_gui.png");
        blit(pPoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (isOverChestArea(pMouseX, pMouseY) || isOverCalculatorArea(pMouseX, pMouseY)) {
            var carried = menu.getCarried();
            if (!carried.isEmpty()
                    && carried.is(ModItems.JEWEL)
                    && pButton == 0
            ) {
                var packet = new PacketInsertCarriedItem(isOverCalculatorArea(pMouseX, pMouseY));
                ModPackets.sendToServer(packet);
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public boolean isOverChestArea(double mouseX, double mouseY) {
        mouseX -= getGuiLeft();
        mouseY -= getGuiTop();
        return RenderUtils.inBounds(7, 17, 72, 108, mouseX, mouseY);
    }

    public boolean isOverCalculatorArea(double mouseX, double mouseY) {
        mouseX -= getGuiLeft();
        mouseY -= getGuiTop();
        return RenderUtils.inBounds(121, 17, 54, 108, mouseX, mouseY);
    }

}
