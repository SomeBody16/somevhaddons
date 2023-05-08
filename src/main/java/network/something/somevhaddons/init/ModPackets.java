package network.something.somevhaddons.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import network.something.somevhaddons.SomeVHAddons;
import network.something.somevhaddons.block.jewel_station.packet.PacketInsertCarriedItem;

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

        CHANNEL.messageBuilder(PacketInsertCarriedItem.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketInsertCarriedItem::new)
                .encoder(PacketInsertCarriedItem::toBytes)
                .consumer(PacketInsertCarriedItem::handle)
                .add();
    }

    public static <TMessage> void sendToServer(TMessage message) {
        CHANNEL.sendToServer(message);
    }

    public static <TMessage> void sendToPlayer(TMessage message, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}
