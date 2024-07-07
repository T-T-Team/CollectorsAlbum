package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;

public final class ItemDropProviderType<I extends ItemDropProvider> {

    public static final Codec<ItemDropProvider> INSTANCE_CODEC = CollectorsAlbumRegistries.ITEM_DROP_PROVIDER.byNameCodec().dispatch(ItemDropProvider::getType, ItemDropProviderType::codec);
    private final MapCodec<I> mapCodec;

    public ItemDropProviderType(MapCodec<I> mapCodec) {
        this.mapCodec = mapCodec;
    }

    public MapCodec<I> codec() {
        return mapCodec;
    }
}
