package network.something.somevhaddons.block.jewel_station.renderer;

import net.minecraft.resources.ResourceLocation;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.block.jewel_station.JewelStationBlockEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class JewelStationModel extends AnimatedGeoModel<JewelStationBlockEntity> {
    @Override
    public ResourceLocation getModelLocation(JewelStationBlockEntity object) {
        return new ResourceLocation(SomeVHAddons.ID, "geo/jewel_station.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(JewelStationBlockEntity object) {
        return new ResourceLocation(SomeVHAddons.ID, "textures/block/jewel_station.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(JewelStationBlockEntity animatable) {
        return new ResourceLocation(SomeVHAddons.ID, "animations/jewel_station.animation.json");
    }
}
