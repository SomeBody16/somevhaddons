package network.something.somevhaddons.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.addon.shard_pouch.ShardPouchBlock;
import network.something.somevhaddons.addon.shard_pouch.ShardPouchBlockEntity;

public class ModBlockEntities {

    protected static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, SomeVHAddons.ID);

    static {
        // Shard Pouch
        ShardPouchBlockEntity.TYPE = BLOCK_ENTITIES.register(ShardPouchBlockEntity.ID,
                () -> BlockEntityType.Builder.of(ShardPouchBlockEntity::new,
                                ShardPouchBlock.TYPE.get())
                        .build(null));
    }

    public static void busRegister(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
