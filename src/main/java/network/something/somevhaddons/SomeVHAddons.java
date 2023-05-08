package network.something.somevhaddons;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import network.something.somevhaddons.init.ModBlockEntities;
import network.something.somevhaddons.init.ModBlocks;
import network.something.somevhaddons.init.ModItems;
import network.something.somevhaddons.init.ModMenuTypes;
import org.slf4j.Logger;

@Mod(SomeVHAddons.ID)
public class SomeVHAddons {
    public static final String ID = "somevhaddons";

    public static final Logger LOGGER = LogUtils.getLogger();

    public SomeVHAddons() {
        var eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.busRegister(eventBus);
        ModBlockEntities.busRegister(eventBus);
        ModItems.busRegister(eventBus);
        ModMenuTypes.busRegister(eventBus);
    }
}
