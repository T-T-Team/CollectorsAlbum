package team.tnt.collectoralbum.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.*;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.network.api.IPacket;
import team.tnt.collectoralbum.network.packet.*;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;

public class Networking {

    private static final int VERSION = 1;
    private static final SimpleChannel NETWORK_CHANNEL = ChannelBuilder.named(new ResourceLocation(CollectorsAlbum.MODID, "network"))
            .networkProtocolVersion(1)
            .clientAcceptedVersions((status, version) -> status == Channel.VersionTest.Status.PRESENT && version == VERSION)
            .serverAcceptedVersions((status, version) -> status == Channel.VersionTest.Status.PRESENT && version == VERSION)
            .simpleChannel();

    // Packet dispatching
    // --------------------------------------------------------------------------

    public static void dispatchServerPacket(IPacket<?> packet) {
        NETWORK_CHANNEL.send(packet, PacketDistributor.SERVER.noArg());
    }

    public static void dispatchClientPacket(ServerPlayer serverPlayerRef, IPacket<?> packet) {
        NETWORK_CHANNEL.send(packet, PacketDistributor.PLAYER.with(serverPlayerRef));
    }

    // Packet registration
    // --------------------------------------------------------------------------

    public static void registerPackets() {
        registerPacket(OpenCardScreenPacket.class);
        registerPacket(RequestAlbumPagePacket.class);
        registerPacket(RequestCardPackDropPacket.class);
        registerPacket(SendAlbumBoostsPacket.class);
    }

    // Internal
    // --------------------------------------------------------------------------

    private static <T extends IPacket<T>> void registerPacket(Class<T> packetClass) {
        T instance;
        try {
            instance = packetClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        NETWORK_CHANNEL.messageBuilder(packetClass)
                .encoder(IPacket::encode)
                .decoder(instance::decode)
                .consumerNetworkThread((BiConsumer<T, CustomPayloadEvent.Context>) IPacket::handle)
                .add();
    }
}
