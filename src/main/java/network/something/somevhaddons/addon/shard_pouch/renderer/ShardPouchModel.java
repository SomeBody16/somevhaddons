package network.something.somevhaddons.addon.shard_pouch.renderer;

import net.minecraft.resources.ResourceLocation;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.addon.shard_pouch.ShardPouchBlockEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ShardPouchModel extends AnimatedGeoModel<ShardPouchBlockEntity> {
    @Override
    public ResourceLocation getModelLocation(ShardPouchBlockEntity object) {
        return new ResourceLocation(SomeVHAddons.ID, "geo/shard_pouch_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ShardPouchBlockEntity object) {
        return new ResourceLocation(SomeVHAddons.ID, "textures/block/shard_pouch_block.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ShardPouchBlockEntity animatable) {
        return new ResourceLocation(SomeVHAddons.ID, "animations/shard_pouch_block.animation.json");
    }
}
