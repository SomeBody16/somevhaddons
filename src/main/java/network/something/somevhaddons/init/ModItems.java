package network.something.somevhaddons.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.addon.experience_nugget.ExperienceNuggetItem;

import java.util.function.Supplier;

public class ModItems {

    protected static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SomeVHAddons.ID);

    public static RegistryObject<Item> EXPERIENCE_NUGGET_2 =
            registerItem("experience_nugget_2", () -> new ExperienceNuggetItem(27));
    public static RegistryObject<Item> EXPERIENCE_NUGGET_3 =
            registerItem("experience_nugget_3", () -> new ExperienceNuggetItem(243));
    public static RegistryObject<Item> EXPERIENCE_NUGGET_4 =
            registerItem("experience_nugget_4", () -> new ExperienceNuggetItem(1395));

    public static void busRegister(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static <T extends Item> RegistryObject<T> registerItem(String id, Supplier<T> supplier) {
        return ITEMS.register(id, supplier);
    }

}
