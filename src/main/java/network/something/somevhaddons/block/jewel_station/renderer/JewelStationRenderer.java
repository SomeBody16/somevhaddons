package network.something.somevhaddons.block.jewel_station.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.block.jewel_station.JewelStationBlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

@Mod.EventBusSubscriber(modid = SomeVHAddons.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JewelStationRenderer extends GeoBlockRenderer<JewelStationBlockEntity> {

    @SubscribeEvent
    public static void register(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(JewelStationBlockEntity.TYPE.get(), JewelStationRenderer::new);
    }

    public JewelStationRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(rendererProvider, new JewelStationModel());
    }

    @Override
    public RenderType getRenderType(JewelStationBlockEntity animatable, float partialTick, PoseStack poseStack,
                                    @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                                    int packedLight, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
