package network.something.somevhaddons.addon.shard_pouch.packet;

import iskallia.vault.item.ItemShardPouch;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import network.something.somevhaddons.addon.shard_pouch.ShardPouches;
import network.something.somevhaddons.init.ModPackets;

import java.util.function.Supplier;

public class PacketRequestShardCount {

    public PacketRequestShardCount() {
    }

    public PacketRequestShardCount(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        var ctx = supplier.get();
        ctx.enqueueWork(() -> {
            var player = ctx.getSender();
            if (player == null) return;
            var pos = player.getOnPos();

            var count = 0;
            for (var pouch : ShardPouches.getPouches(player.level, pos)) {
                count += ItemShardPouch.getContainedStack(pouch).getCount();
            }

            var packet = new PacketShardCount(count, pos);
            ModPackets.sendToPlayer(packet, player);
        });
        return true;
    }
}
