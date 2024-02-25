package network.something.somevhaddons.addon.shard_pouch;

import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemShardPouch;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.RegistryObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;

import static net.minecraft.Util.NIL_UUID;

public class ShardPouchBlockEntity extends BlockEntity implements IAnimatable {
    public static final String ID = "shard_pouch_block";
    public static RegistryObject<BlockEntityType<ShardPouchBlockEntity>> TYPE;

    protected AnimationFactory factory = GeckoLibUtil.createFactory(this);
    protected ItemStack shardPouch = ItemStack.EMPTY;
    protected LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public ShardPouchBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TYPE.get(), pPos, pBlockState);
    }


    @Override
    public void registerControllers(AnimationData data) {
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public void setShardPouch(ItemStack stack) {
        if (stack.is(ModItems.SHARD_POUCH)) {
            this.shardPouch = stack;
        }
    }

    public int getAmount() {
        return ItemShardPouch.getContainedStack(shardPouch).getCount();
    }

    public void insert(int amount) {
        var shards = new ItemStack(ModItems.SOUL_SHARD, amount);
        insert(shards);
    }

    public void insert(ItemStack shards) {
        shardPouch.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            handler.insertItem(0, shards, false);
        });
    }

    public ItemStack extract(int amount) {
        return shardPouch.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .resolve()
                .map(handler -> handler.extractItem(0, amount, false))
                .orElse(ItemStack.EMPTY);
    }

    public void donate(Player player) {
        var amount = ItemShardPouch.getShardCount(player);
        insert(amount);
        ItemShardPouch.reduceShardAmount(player.getInventory(), amount, false);

        if (player instanceof ServerPlayer serverPlayer) {
            var newAmount = getAmount();
            var txt = "%d soul shards donated (%d total)".formatted(amount, newAmount);
            var message = new TextComponent(txt).withStyle(ChatFormatting.LIGHT_PURPLE);
            serverPlayer.sendMessage(message, ChatType.GAME_INFO, NIL_UUID);
        }
    }

    @NonNull
    @Override
    public <T> LazyOptional<T> getCapability(@NonNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> new ItemShardPouch.Handler(shardPouch) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setChanged();
            }
        });
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        var shardPouchTag = new CompoundTag();
        shardPouch.save(shardPouchTag);
        tag.put("ShardPouch", shardPouchTag);

        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        var shardPouchTag = nbt.getCompound("ShardPouch");
        shardPouch = ItemStack.of(shardPouchTag);

        super.load(nbt);
    }
}
