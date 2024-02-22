package network.something.somevhaddons.addon.shard_pouch;

import iskallia.vault.item.ItemShardPouch;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
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

import static net.minecraft.Util.NIL_UUID;

public class ShardPouchBlock extends BaseEntityBlock {
    public static final String ID = "shard_pouch_block";
    public static RegistryObject<ShardPouchBlock> TYPE;

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ShardPouchBlock() {
        super(
                Properties.of(Material.WOOL)
                        .sound(SoundType.WOOL)
                        .instabreak()
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
        if (pLevel.getBlockEntity(pPos) instanceof ShardPouchBlockEntity blockEntity) {
            if (pPlayer.isCrouching()) {
                // Donate all your soul shards
                var amount = ItemShardPouch.getShardCount(pPlayer);
                blockEntity.insert(amount);
                ItemShardPouch.reduceShardAmount(pPlayer.getInventory(), amount, false);

                if (pPlayer instanceof ServerPlayer serverPlayer) {
                    var newAmount = blockEntity.getAmount();
                    var txt = "%d soul shards donated (%d total)".formatted(amount, newAmount);
                    var message = new TextComponent(txt).withStyle(ChatFormatting.LIGHT_PURPLE);
                    serverPlayer.sendMessage(message, ChatType.GAME_INFO, NIL_UUID);
                }

                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            } else {
                // Take one stack of soul shards
                var shards = blockEntity.extract(64);
                Block.popResourceFromFace(pLevel, pPos, pHit.getDirection(), shards);

                if (pPlayer instanceof ServerPlayer serverPlayer) {
                    var newAmount = blockEntity.getAmount();
                    var txt = "%d soul shards taken (%d left)".formatted(shards.getCount(), newAmount);
                    var message = new TextComponent(txt).withStyle(ChatFormatting.LIGHT_PURPLE);
                    serverPlayer.sendMessage(message, ChatType.GAME_INFO, NIL_UUID);
                }

                return InteractionResult.sidedSuccess(pLevel.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }
}
