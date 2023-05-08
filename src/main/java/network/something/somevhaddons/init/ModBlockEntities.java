package network.something.somevhaddons.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.block.jewel_station.JewelStationBlock;
import network.something.somevhaddons.block.jewel_station.JewelStationBlockEntity;

public class ModBlockEntities {

    protected static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, SomeVHAddons.ID);

    static {
        // Jewel Station
        JewelStationBlockEntity.TYPE = BLOCK_ENTITIES.register(JewelStationBlockEntity.ID,
                () -> BlockEntityType.Builder.of(JewelStationBlockEntity::new,
                                JewelStationBlock.TYPE.get())
                        .build(null));
    }

    public static void busRegister(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
