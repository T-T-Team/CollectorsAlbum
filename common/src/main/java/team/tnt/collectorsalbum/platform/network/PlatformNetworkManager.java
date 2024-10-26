package team.tnt.collectorsalbum.platform.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import team.tnt.collectorsalbum.platform.Identifiable;
import team.tnt.collectorsalbum.platform.JavaServiceLoader;
import team.tnt.collectorsalbum.platform.Side;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public static PlatformNetworkManager create(ResourceLocation identifier) {
        return new PlatformNetworkManager(identifier);
    }

    public static PlatformNetworkManager create(String namespace) {
        return create(new ResourceLocation(namespace, "network"));
    }

    public <T extends NetworkMessage> void registerPacket(PacketDirection direction, ResourceLocation pid, Class<T> packet, Function<FriendlyByteBuf, T> decoder) {
        this.registry.register(direction, new PacketHolder<>(pid, packet, decoder));
    }

    public void bind(Side side) {
        List<PacketHolder<?, ?>> c2s = new ArrayList<>();
        List<PacketHolder<?, ?>> s2c = new ArrayList<>();
        this.bindRef(c2s::addAll, s2c::addAll);
        NETWORK.initialize(this.identifier, side, c2s, s2c);
    }

    public void bindRef(Consumer<List<PacketHolder<?, ?>>> c2sPackets, Consumer<List<PacketHolder<?, ?>>> s2cPackets) {
        List<PacketHolder<?, ?>> c2s = registry.sidePackets.get(PacketDirection.CLIENT_TO_SERVER);
        List<PacketHolder<?, ?>> s2c = registry.sidePackets.get(PacketDirection.SERVER_TO_CLIENT);
        if (!c2s.isEmpty())
            c2sPackets.accept(c2s);
        if (!s2c.isEmpty())
            s2cPackets.accept(s2c);
    }

    public void close() {
        this.registry = null;
    }

    private static final class Registry {

        private final EnumMap<PacketDirection, List<PacketHolder<?, ?>>> sidePackets;

        private Registry() {
            this.sidePackets = new EnumMap<>(PacketDirection.class);
            this.sidePackets.put(PacketDirection.CLIENT_TO_SERVER, new ArrayList<>());
            this.sidePackets.put(PacketDirection.SERVER_TO_CLIENT, new ArrayList<>());
        }

        void register(PacketDirection direction, PacketHolder<?, ?> packet) {
            this.sidePackets.get(direction).add(packet);
        }
    }
}
