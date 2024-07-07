package team.tnt.collectorsalbum.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;
import team.tnt.collectorsalbum.platform.resource.PlatformGsonCodecReloadListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public final class S2C_SendDatapackResources<T> implements CustomPacketPayload {

    private static final Map<ResourceLocation, PlatformGsonCodecReloadListener<?>> REGISTERED_TYPES = new HashMap<>();
    private static final ResourceLocation IDENTIFIER = PlatformNetworkManager.generatePacketIdentifier(CollectorsAlbum.MOD_ID, S2C_SendDatapackResources.class);
    public static final Type<S2C_SendDatapackResources> TYPE = new Type<>(IDENTIFIER);
    public static final StreamCodec<FriendlyByteBuf, S2C_SendDatapackResources> CODEC = StreamCodec.of(
            (buf, payload) -> payload.encode(buf),
            S2C_SendDatapackResources::decode
    );

    private final PlatformGsonCodecReloadListener<T> listener;
    private ValueHolder<T> holder;

    public S2C_SendDatapackResources(PlatformGsonCodecReloadListener<T> listener) {
        this.listener = listener;
    }

    private S2C_SendDatapackResources(PlatformGsonCodecReloadListener<T> listener, ValueHolder<T> holder) {
        this.listener = listener;
        this.holder = holder;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private void encode(FriendlyByteBuf buffer) {
        CollectorsAlbum.LOGGER.debug("Sending datapack data to client for {} resource manager", listener.identifier());
        buffer.writeResourceLocation(listener.identifier());
        List<T> collection = listener.getNetworkData();
        ValueHolder<T> holder = new ValueHolder<>(collection);
        Codec<ValueHolder<T>> codec = ValueHolder.codec(listener.codec());
        DataResult<Tag> result = codec.encodeStart(NbtOps.INSTANCE, holder);
        Tag tag = result.getOrThrow();
        buffer.writeNbt(tag);
    }

    @SuppressWarnings("unchecked")
    private static <T> S2C_SendDatapackResources<T> decode(FriendlyByteBuf buffer) {
        ResourceLocation location = buffer.readResourceLocation();
        CollectorsAlbum.LOGGER.debug("Receiving datapack data from server for {} resource manager", location);
        PlatformGsonCodecReloadListener<T> listener = (PlatformGsonCodecReloadListener<T>) REGISTERED_TYPES.get(location);
        if (listener == null) {
            throw new UnsupportedOperationException("Reload listener " + location + " is not registered!");
        }
        Codec<ValueHolder<T>> codec = ValueHolder.codec(listener.codec());
        DataResult<ValueHolder<T>> result = codec.parse(NbtOps.INSTANCE, buffer.readNbt());
        ValueHolder<T> data = result.getOrThrow();
        return new S2C_SendDatapackResources<>(listener, data);
    }

    public void onDataReceived(Player player) {
        CollectorsAlbum.LOGGER.debug("Resolved {} entries from server for {} resource manager, importing...", holder.values().size(), listener.identifier());
        listener.onNetworkDataReceived(holder.values());
    }

    public static void registerType(PlatformGsonCodecReloadListener<?> listener) {
        REGISTERED_TYPES.put(listener.identifier(), listener);
    }

    private record ValueHolder<T>(List<T> values) {

        static <T> Codec<ValueHolder<T>> codec(Codec<T> element) {
                return RecordCodecBuilder.create(instance -> instance.group(
                        element.listOf().fieldOf("valueList").forGetter(t -> t.values)
                ).apply(instance, ValueHolder::new));
            }
        }
}
