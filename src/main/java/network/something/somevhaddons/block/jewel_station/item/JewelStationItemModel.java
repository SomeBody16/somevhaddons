package network.something.somevhaddons.block.jewel_station.item;

import net.minecraft.resources.ResourceLocation;
import network.something.somevhaddons.SomeVHAddons;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class JewelStationItemModel extends AnimatedGeoModel<JewelStationItem> {
    @Override
    public ResourceLocation getModelLocation(JewelStationItem object) {
        return new ResourceLocation(SomeVHAddons.ID, "geo/jewel_station.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(JewelStationItem object) {
        return new ResourceLocation(SomeVHAddons.ID, "textures/block/jewel_station.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(JewelStationItem animatable) {
        return new ResourceLocation(SomeVHAddons.ID, "animations/jewel_station.animation.json");
    }
}
