package network.something.somevhaddons.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.addon.shard_pouch.ShardPouchBlock;

import java.util.function.Supplier;

public class ModBlocks {

    protected static DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, SomeVHAddons.ID);

    static {
        // Shard Pouch
        ShardPouchBlock.TYPE = registerBlock(ShardPouchBlock.ID, ShardPouchBlock::new);
    }

    public static void busRegister(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    public static <T extends Block> RegistryObject<T> registerBlock(String id, Supplier<T> supplier) {
        return BLOCKS.register(id, supplier);
    }

    public static <T extends Block> RegistryObject<T> registerBlockWithItem(String id, Supplier<T> supplier,
                                                                            CreativeModeTab tab) {
        var block = registerBlock(id, supplier);
        registerBlockItem(id, block, tab);
        return block;
    }

    protected static <T extends Block> RegistryObject<Item> registerBlockItem(String id, RegistryObject<T> block,
                                                                              CreativeModeTab tab) {
        Supplier<Item> supplier = () -> new BlockItem(
                block.get(),
                new Item.Properties().tab(tab)
        );
        return ModItems.registerItem(id, supplier);
    }

}
