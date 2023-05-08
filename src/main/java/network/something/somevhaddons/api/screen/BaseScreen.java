package network.something.somevhaddons.api.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseScreen<T extends BaseMenu> extends AbstractContainerScreen<T> {

    protected final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();

    public BaseScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(@NotNull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        this.leftPos = (width - imageWidth) / 2;
        this.topPos = (height - imageHeight) / 2;
        renderBackground(pPoseStack, pPartialTick, pMouseX, pMouseY);
        renderForeground(pPoseStack, pPartialTick, pMouseX, pMouseY);
    }

    public void bindTexture(String namespace, String path) {
        RenderSystem.setShaderTexture(0, TEXTURE_CACHE.computeIfAbsent(
                namespace + ":" + path,
                newId -> new ResourceLocation(namespace, path)
        ));
    }

    protected void renderForeground(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        menu.widgets.forEach(widget -> widget.render(pPoseStack, pMouseX, pMouseY, pPartialTick));
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (var widget : menu.widgets) {
            if (widget instanceof GuiEventListener guiEventListener
                    && guiEventListener.mouseClicked(pMouseX, pMouseY, pButton)) {
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        for (var widget : menu.widgets) {
            if (widget instanceof GuiEventListener guiEventListener
                    && guiEventListener.mouseReleased(pMouseX, pMouseY, pButton)) {
                return true;
            }
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        for (var widget : menu.widgets) {
            if (widget instanceof GuiEventListener guiEventListener) {
                guiEventListener.mouseMoved(pMouseX, pMouseY);
            }
        }
        super.mouseMoved(pMouseX, pMouseY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        for (var widget : menu.widgets) {
            if (widget instanceof GuiEventListener guiEventListener
                    && guiEventListener.mouseScrolled(pMouseX, pMouseY, pDelta)) {
                return true;
            }
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    protected abstract void renderBackground(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY);

}
