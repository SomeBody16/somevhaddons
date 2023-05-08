package network.something.somevhaddons.api.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.api.screen.BaseScreen;
import network.something.somevhaddons.api.util.RenderUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScrollbarWidget extends AbstractWidget implements GuiEventListener {
    public static final int SCROLLBAR_WIDTH = 12;
    public static final int SCROLLBAR_HEIGHT = 15;

    protected List<Consumer<Float>> listeners = new ArrayList<>();
    protected final int height;

    protected int value = 0;
    protected boolean clicked = false;

    public ScrollbarWidget(BaseScreen<?> screen, int x, int y, int height) {
        super(screen, x, y);
        this.height = height;
    }

    public void addListener(Consumer<Float> listener) {
        listeners.add(listener);
    }

    public void notifyListeners() {
        listeners.forEach(listener -> listener.accept(getProgress()));
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        screen.bindTexture(SomeVHAddons.ID, "textures/gui/scrollbar.png");
        screen.blit(pPoseStack,
                screen.getGuiLeft() + x, screen.getGuiTop() + y + value,
                0, 0, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        pMouseX -= screen.getGuiLeft();
        pMouseY -= screen.getGuiTop();

        if (pButton != 0
                || !RenderUtils.inBounds(x, y + value, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT, pMouseX, pMouseY)
        ) {
            return false;
        }

        clicked = true;
        updateValue(pMouseY);
        return true;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (clicked) {
            clicked = false;
            return true;
        }
        return false;
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        if (!clicked) return;

        pMouseY -= screen.getGuiTop();
        updateValue(pMouseY);
    }

    protected void updateValue(double mouseY) {
        double offset = SCROLLBAR_HEIGHT / 2d;
        int newValue = (int) (mouseY - y - offset);

        newValue = Math.max(newValue, 0);
        newValue = Math.min(newValue, getMaxValue());

        value = newValue;
        notifyListeners();
    }

    protected int getMaxValue() {
        return height - SCROLLBAR_HEIGHT;
    }

    public float getProgress() {
        return value / (float) getMaxValue();
    }
}
