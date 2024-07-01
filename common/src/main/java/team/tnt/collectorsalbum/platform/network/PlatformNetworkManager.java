package team.tnt.collectorsalbum.platform.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import team.tnt.collectorsalbum.platform.Identifiable;
import team.tnt.collectorsalbum.platform.JavaServiceLoader;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public final class PlatformNetworkManager implements Identifiable {

    public static final Network NETWORK = JavaServiceLoader.loadService(Network.class);
    private final ResourceLocation identifier;
    private Registry registry = new Registry();

    private PlatformNetworkManager(ResourceLocation identifier) {
        this.identifier = identifier;
    }

    @Override
    public ResourceLocation identifier() {
        return this.identifier;
    }

    public static ResourceLocation generatePacketIdentifier(Identifiable identifiable, Class<? extends CustomPacketPayload> type) {
        return generatePacketIdentifier(identifiable.identifier().getNamespace(), type);
    }

    public static ResourceLocation generatePacketIdentifier(ResourceLocation identifier, Class<? extends CustomPacketPayload> type) {
        return generatePacketIdentifier(identifier.getNamespace(), type);
    }

    public static ResourceLocation generatePacketIdentifier(String namespace, Class<? extends CustomPacketPayload> type) {
        return ResourceLocation.fromNamespaceAndPath(namespace, type.getSimpleName().replaceAll("([A-Z])", "_$1").toLowerCase(Locale.ROOT));
    }

    public static PlatformNetworkManager create(ResourceLocation identifier) {
        return new PlatformNetworkManager(identifier);
    }

    public static PlatformNetworkManager create(String namespace) {
        return create(ResourceLocation.fromNamespaceAndPath(namespace, "network"));
    }

    public <P extends CustomPacketPayload> void registerPacket(PacketDirection direction, Class<P> payloadType, CustomPacketPayload.Type<P> type, StreamCodec<FriendlyByteBuf, P> codec) {
        registerPacket(direction, payloadType, type, codec, null);
    }

    public <P extends CustomPacketPayload> void registerPacket(PacketDirection direction, Class<P> payloadType, CustomPacketPayload.Type<P> type, StreamCodec<FriendlyByteBuf, P> codec, PacketHandler<P> handler) {
        registry.register(direction, new PacketHolder<>(payloadType, type, codec, handler));
    }

    public void bind() {
        List<PacketHolder<?>> c2s = new ArrayList<>();
        List<PacketHolder<?>> s2c = new ArrayList<>();
        this.bindRef(c2s::addAll, s2c::addAll);
        NETWORK.initialize(this.identifier, c2s, s2c);
    }

    public void bindRef(Consumer<List<PacketHolder<?>>> c2sPackets, Consumer<List<PacketHolder<?>>> s2cPackets) {
        List<PacketHolder<?>> c2s = registry.sidePackets.get(PacketDirection.CLIENT_TO_SERVER);
        List<PacketHolder<?>> s2c = registry.sidePackets.get(PacketDirection.SERVER_TO_CLIENT);
        if (!c2s.isEmpty())
            c2sPackets.accept(c2s);
        if (!s2c.isEmpty())
            s2cPackets.accept(s2c);
        registry = null;
    }

    private static final class Registry {

        private final EnumMap<PacketDirection, List<PacketHolder<?>>> sidePackets;

        private Registry() {
            this.sidePackets = new EnumMap<>(PacketDirection.class);
            this.sidePackets.put(PacketDirection.CLIENT_TO_SERVER, new ArrayList<>());
            this.sidePackets.put(PacketDirection.SERVER_TO_CLIENT, new ArrayList<>());
        }

        void register(PacketDirection direction, PacketHolder<?> packet) {
            this.sidePackets.get(direction).add(packet);
        }
    }
}
