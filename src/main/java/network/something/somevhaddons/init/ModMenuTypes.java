package network.something.somevhaddons.init;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.block.jewel_station.gui.JewelStationMenu;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, SomeVHAddons.ID);

    static {
        JewelStationMenu.TYPE = registerMenuType(JewelStationMenu::new, JewelStationMenu.ID);
    }

    public static void busRegister(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

    public static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory,
                                                                                                 String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }
}
