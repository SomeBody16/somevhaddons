package network.something.somevhaddons.addon.shard_pouch;

import iskallia.vault.item.ItemShardPouch;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShardPouchBlock extends BaseEntityBlock {
    public static final String ID = "shard_pouch_block";
    public static RegistryObject<ShardPouchBlock> TYPE;

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ShardPouchBlock() {
        super(
                Properties.of(Material.WOOL)
                        .sound(SoundType.WOOL)
                        .strength(0.8f)
                        .noOcclusion()
        );
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ShardPouchBlockEntity.TYPE.get().create(pPos, pState);
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()
                && pLevel.getBlockEntity(pPos) instanceof ShardPouchBlockEntity blockEntity
        ) {
            blockEntity.drops();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @SuppressWarnings("deprecation")
    @NotNull
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.isCrouching()) {
            var anyHandEmpty = pPlayer.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()
                    || pPlayer.getItemInHand(InteractionHand.OFF_HAND).isEmpty();

            if (!pLevel.isClientSide && anyHandEmpty) {
                pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 11);

                SoundType soundtype = pState.getSoundType(pLevel, pPos, pPlayer);
                pLevel.playSound(null, pPos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS,
                        (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }

        if (pPlayer instanceof ServerPlayer serverPlayer
                && pLevel.getBlockEntity(pPos) instanceof ShardPouchBlockEntity blockEntity
        ) {
            var amount = ItemShardPouch.getContainedStack(blockEntity.shardPouch).getCount();
            var message = new TextComponent(Integer.toString(amount))
                    .withStyle(ChatFormatting.LIGHT_PURPLE);
            var packet = new ClientboundSetActionBarTextPacket(message);
            serverPlayer.connection.send(packet);
        }

        return InteractionResult.PASS;
    }
}
