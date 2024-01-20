package network.something.somevhaddons.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.addon.shard_pouch.packet.PacketRequestShardCount;
import network.something.somevhaddons.addon.shard_pouch.packet.PacketShardClear;
import network.something.somevhaddons.addon.shard_pouch.packet.PacketShardCount;

public class ModPackets {

    private static SimpleChannel CHANNEL;

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        CHANNEL = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(SomeVHAddons.ID, "packet"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        CHANNEL.messageBuilder(PacketRequestShardCount.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketRequestShardCount::new)
                .encoder(PacketRequestShardCount::toBytes)
                .consumer(PacketRequestShardCount::handle)
                .add();

        CHANNEL.messageBuilder(PacketShardClear.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketShardClear::new)
                .encoder(PacketShardClear::toBytes)
                .consumer(PacketShardClear::handle)
                .add();

        CHANNEL.messageBuilder(PacketShardCount.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketShardCount::new)
                .encoder(PacketShardCount::toBytes)
                .consumer(PacketShardCount::handle)
                .add();
    }

    public static <TMessage> void sendToServer(TMessage message) {
        CHANNEL.sendToServer(message);
    }

    public static <TMessage> void sendToPlayer(TMessage message, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}
