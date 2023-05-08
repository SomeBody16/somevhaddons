package network.something.somevhaddons.api.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import network.something.somevhaddons.api.screen.BaseScreen;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractWidget {
    public abstract void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick);

    protected final BaseScreen<?> screen;
    protected final int x;
    protected final int y;

    public AbstractWidget(BaseScreen<?> screen, int x, int y) {
        this.screen = screen;
        this.x = x;
        this.y = y;
    }
}
