package team.tnt.collectorsalbum.common.resource.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;

public final class NumberProviderType<P extends NumberProvider> {

    public static final Codec<NumberProvider> INSTANCE_CODEC = CollectorsAlbumRegistries.NUMBER_PROVIDER.byNameCodec().dispatch(NumberProvider::getType, t -> t.codec.codec());

    private final MapCodec<P> codec;

    public NumberProviderType(final MapCodec<P> codec) {
        this.codec = codec;
    }
}
