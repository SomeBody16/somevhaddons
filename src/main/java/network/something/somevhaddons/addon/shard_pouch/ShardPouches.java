package network.something.somevhaddons.addon.shard_pouch;

import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemShardPouch;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import network.something.somevhaddons.addon.shard_pouch.packet.PacketRequestShardCount;
import network.something.somevhaddons.init.ModPackets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShardPouches {

    public static final int SCAN_RADIUS = 5;
    protected static final HashMap<BlockPos, Integer> AMOUNT_IN_POSITION = new HashMap<>();

    public static void setAmountInPosition(BlockPos pos, int amount) {
        AMOUNT_IN_POSITION.put(pos, amount);
    }

    public static int getAmountInPosition(Player player) {
        if (player.level.isClientSide) {
            var request = new PacketRequestShardCount();
            ModPackets.sendToServer(request);
            return AMOUNT_IN_POSITION.getOrDefault(player.getOnPos(), 0);
        }

        var count = 0;
        for (var pouch : ShardPouches.getPouches(player.level, player.getOnPos())) {
            count += ItemShardPouch.getContainedStack(pouch).getCount();
        }
        return count;
    }

    public static void clear() {
        AMOUNT_IN_POSITION.clear();
    }

    public static List<ItemStack> getPouches(Level level, BlockPos center) {
        var result = new ArrayList<ItemStack>();
        for (var x = -SCAN_RADIUS; x <= SCAN_RADIUS; x++) {
            for (var y = -SCAN_RADIUS; y <= SCAN_RADIUS; y++) {
                for (var z = -SCAN_RADIUS; z <= SCAN_RADIUS; z++) {
                    var blockPos = center.offset(x, y, z);
                    if (level.getBlockEntity(blockPos) instanceof ShardPouchBlockEntity blockEntity
                            && blockEntity.getShardPouch() != null
                            && blockEntity.getShardPouch().is(ModItems.SHARD_POUCH)
                    ) {
                        result.add(blockEntity.getShardPouch());
                    }
                }
            }
        }
        return result;
    }

}
