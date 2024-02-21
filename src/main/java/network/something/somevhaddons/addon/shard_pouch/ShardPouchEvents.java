package network.something.somevhaddons.addon.shard_pouch;

import iskallia.vault.init.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import network.something.somevhaddons.SomeVHAddons;

@Mod.EventBusSubscriber(modid = SomeVHAddons.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShardPouchEvents {

    @SubscribeEvent
    public static void rightClickItem(final PlayerInteractEvent.RightClickItem event) {
//        if (event.getItemStack().is(ModItems.SHARD_POUCH)
//                && event.getPlayer().isCrouching()
//        ) {
//            event.setCanceled(true);
//        }
    }

    @SubscribeEvent
    public static void rightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().is(ModItems.SHARD_POUCH)
                || !event.getPlayer().isCrouching()
        ) {
            return;
        }
        event.setCanceled(true);

        var shardPouch = event.getItemStack().copy();
        var ctx = new BlockPlaceContext(event.getPlayer(), event.getHand(),
                event.getItemStack(), event.getHitVec());

        if (place(ctx, shardPouch)) {
            event.getPlayer().setItemInHand(event.getHand(), ItemStack.EMPTY);
        }
    }


    public static boolean place(BlockPlaceContext ctx, ItemStack shardPouch) {
        var blockPos = ctx.getClickedPos();
        var blockState = ShardPouchBlock.TYPE.get().getStateForPlacement(ctx);
        var level = ctx.getLevel();
        var player = ctx.getPlayer();

        if (blockState == null
                || !canPlace(ctx, blockState)
                || !ctx.getLevel().setBlock(blockPos, blockState, 11)
        ) {
            return false;
        }

        level.gameEvent(player, GameEvent.BLOCK_PLACE, blockPos);
        SoundType soundtype = blockState.getSoundType(level, blockPos, ctx.getPlayer());
        level.playSound(player, blockPos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

        if (level.getBlockEntity(blockPos) instanceof ShardPouchBlockEntity blockEntity) {
            blockEntity.setShardPouch(shardPouch);
        }
        return true;
    }

    protected static boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        Player player = pContext.getPlayer();
        CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        return (pState.canSurvive(pContext.getLevel(), pContext.getClickedPos())) && pContext.getLevel().isUnobstructed(pState, pContext.getClickedPos(), collisioncontext);
    }

}
