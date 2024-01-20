package network.something.somevhaddons.addon.shard_pouch.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import network.something.somevhaddons.addon.shard_pouch.ShardPouches;

import java.util.function.Supplier;

public class PacketShardClear {

    public PacketShardClear() {
    }

    public PacketShardClear(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        var ctx = supplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ShardPouches::clear));
        return true;
    }
}
