package team.tnt.collectorsalbum.platform.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface NetworkCodec<T> {

    NetworkCodec<ResourceLocation> RESOURCE_LOCATION = create(FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::readResourceLocation);

    void encode(FriendlyByteBuf buffer, T element);

    T decode(FriendlyByteBuf buffer);

    static <T> NetworkCodec<T> create(BiConsumer<FriendlyByteBuf, T> encoder, Function<FriendlyByteBuf, T> decoder) {
        return new NetworkCodec<>() {
            @Override
            public void encode(FriendlyByteBuf buffer, T element) {
                encoder.accept(buffer, element);
            }

            @Override
            public T decode(FriendlyByteBuf buffer) {
                return decoder.apply(buffer);
            }
        };
    }
}
