package team.tnt.collectoralbum.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.network.api.IPacket;
import team.tnt.collectoralbum.network.packet.*;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class Networking {

    private static final String VERSION = "1";
    private static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(CollectorsAlbum.MODID, "network"))
            .networkProtocolVersion(() -> VERSION)
            .clientAcceptedVersions(VERSION::equals)
            .serverAcceptedVersions(VERSION::equals)
            .simpleChannel();
    private static int packetDescriptor;

    // Packet dispatching
    // --------------------------------------------------------------------------

    public static void dispatchServerPacket(IPacket<?> packet) {
        NETWORK_CHANNEL.send(PacketDistributor.SERVER.noArg(), packet);
    }

    public static void dispatchClientPacket(ServerPlayer serverPlayerRef, IPacket<?> packet) {
        NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayerRef), packet);
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
        NETWORK_CHANNEL.messageBuilder(packetClass, packetDescriptor++)
                .encoder(IPacket::encode)
                .decoder(instance::decode)
                .consumerNetworkThread((BiConsumer<T, Supplier<NetworkEvent.Context>>) IPacket::handle)
                .add();
    }
}
