package network.something.somevhaddons.addon.shard_pouch.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import network.something.somevhaddons.addon.shard_pouch.ShardPouches;

import java.util.function.Supplier;

public class PacketShardCount {

    protected final int soulShardCount;
    protected final BlockPos blockPos;

    public PacketShardCount(int soulShardCount, BlockPos blockPos) {
        this.soulShardCount = soulShardCount;
        this.blockPos = blockPos;
    }

    public PacketShardCount(FriendlyByteBuf buf) {
        this.soulShardCount = buf.readInt();
        this.blockPos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(soulShardCount);
        buf.writeBlockPos(blockPos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        var ctx = supplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ShardPouches.setAmountInPosition(blockPos, soulShardCount);
        }));
        return true;
    }
}
