package network.something.somevhaddons.api.screen;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import network.something.somevhaddons.api.screen.widget.AbstractWidget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMenu extends AbstractContainerMenu {

    public final List<AbstractWidget> widgets = new ArrayList<>();

    protected BaseMenu(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }
}
