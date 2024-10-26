package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;

public final class AlbumBonusType<B extends AlbumBonus> {

    public static final Codec<AlbumBonus> INSTANCE_CODEC = CollectorsAlbumRegistries.ALBUM_BONUS.byNameCodec().dispatch(AlbumBonus::getType, t -> t.codec.codec());

    private final MapCodec<B> codec;

    public AlbumBonusType(MapCodec<B> codec) {
        this.codec = codec;
    }
}
