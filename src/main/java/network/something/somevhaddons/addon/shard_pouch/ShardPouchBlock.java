package network.something.somevhaddons.addon.shard_pouch;

import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemShardPouch;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

import static net.minecraft.Util.NIL_UUID;

public class ShardPouchBlock extends BaseEntityBlock {
    public static final String ID = "shard_pouch_block";
    public static RegistryObject<ShardPouchBlock> TYPE;

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Stream.of(
            Block.box(3, 0, 3, 13, 5, 13),
            Block.box(4, 5, 4, 12, 6, 12),
            Block.box(5, 6, 5, 11, 7, 11),
            Block.box(6, 7, 6, 10, 8, 10),
            Block.box(6, 8, 6, 10, 9, 7),
            Block.box(6, 8, 9, 10, 9, 10),
            Block.box(9, 8, 7, 10, 9, 9),
            Block.box(6, 8, 7, 7, 9, 9)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public ShardPouchBlock() {
        super(
                Properties.of(Material.WOOL)
                        .sound(SoundType.WOOL)
                        .instabreak()
                        .noOcclusion()
        );
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
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
                && blockEntity.shardPouch.is(ModItems.SHARD_POUCH)
        ) {
            ShardPouchBlock.popResourceFromFace(pLevel, pPos, Direction.UP, blockEntity.shardPouch);
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
                popResourceFromFace(pLevel, pPos, pHit.getDirection(), shards);

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


    public static void popResourceFromFace(Level pLevel, BlockPos pPos, Direction pDirection, ItemStack pStack) {
        int i = pDirection.getStepX();
        int j = pDirection.getStepY();
        int k = pDirection.getStepZ();
        float f = EntityType.ITEM.getWidth() / 2.0F;
        float f1 = EntityType.ITEM.getHeight() / 2.0F;
        double d0 = (double) ((float) pPos.getX() + 0.5F) + (i == 0 ? Mth.nextDouble(pLevel.random, -0.25D, 0.25D) : (double) ((float) i * (0.5F + f)));
        double d1 = (double) ((float) pPos.getY() + 0.5F) + (j == 0 ? Mth.nextDouble(pLevel.random, -0.25D, 0.25D) : (double) ((float) j * (0.5F + f1))) - (double) f1;
        double d2 = (double) ((float) pPos.getZ() + 0.5F) + (k == 0 ? Mth.nextDouble(pLevel.random, -0.25D, 0.25D) : (double) ((float) k * (0.5F + f)));
        double d3 = i == 0 ? Mth.nextDouble(pLevel.random, -0.1D, 0.1D) : (double) i * 0.1D;
        double d4 = j == 0 ? Mth.nextDouble(pLevel.random, 0.0D, 0.1D) : (double) j * 0.1D + 0.1D;
        double d5 = k == 0 ? Mth.nextDouble(pLevel.random, -0.1D, 0.1D) : (double) k * 0.1D;

        if (!pLevel.isClientSide && !pStack.isEmpty() && pLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !pLevel.restoringBlockSnapshots) {
            var itementity = new ItemEntity(pLevel, d0, d1, d2, pStack, d3, d4, d5);
            itementity.setNoPickUpDelay();
            pLevel.addFreshEntity(itementity);
        }
    }
}
