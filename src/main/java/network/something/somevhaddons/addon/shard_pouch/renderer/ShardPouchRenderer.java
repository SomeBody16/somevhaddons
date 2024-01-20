package network.something.somevhaddons.addon.shard_pouch.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.addon.shard_pouch.ShardPouchBlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

@Mod.EventBusSubscriber(modid = SomeVHAddons.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ShardPouchRenderer extends GeoBlockRenderer<ShardPouchBlockEntity> {

    @SubscribeEvent
    public static void register(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ShardPouchBlockEntity.TYPE.get(), ShardPouchRenderer::new);
    }

    public ShardPouchRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        super(rendererProvider, new ShardPouchModel());
    }

    @Override
    public RenderType getRenderType(ShardPouchBlockEntity animatable, float partialTick, PoseStack poseStack,
                                    @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                                    int packedLight, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
