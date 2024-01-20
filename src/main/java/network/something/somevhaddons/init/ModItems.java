package network.something.somevhaddons.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import network.something.somevhaddons.SomeVHAddons;

import java.util.function.Supplier;

public class ModItems {

    protected static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SomeVHAddons.ID);

    static {
    }

    public static void busRegister(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static <T extends Item> RegistryObject<T> registerItem(String id, Supplier<T> supplier) {
        return ITEMS.register(id, supplier);
    }

}
