package network.something.somevhaddons.block.jewel_station.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.registries.RegistryObject;
import network.something.somevhaddons.block.jewel_station.JewelStationBlock;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.function.Consumer;

public class JewelStationItem extends BlockItem implements IAnimatable {
    public static final String ID = "jewel_station";
    public static RegistryObject<JewelStationItem> TYPE;

    protected AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public JewelStationItem() {
        super(
                JewelStationBlock.TYPE.get(),
                new Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)
        );
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(
                new AnimationController<>(this, "controller",
                        0, this::predicate));
    }

    protected <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(
                new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP)
        );
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IItemRenderProperties() {
            private final BlockEntityWithoutLevelRenderer renderer = new JewelStationItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer;
            }
        });
    }

}
