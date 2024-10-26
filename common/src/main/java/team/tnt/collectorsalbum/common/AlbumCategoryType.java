package team.tnt.collectorsalbum.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public final class AlbumCategoryType<C extends AlbumCategory> {

    public static final Codec<AlbumCategory> INSTANCE_CODEC = CollectorsAlbumRegistries.CATEGORY.byNameCodec().dispatch(AlbumCategory::getType, t -> t.codec().codec());
    private final MapCodec<C> codec;

    public AlbumCategoryType(MapCodec<C> codec) {
        this.codec = codec;
    }

    public MapCodec<C> codec() {
        return codec;
    }
}
