package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;

public final class AlbumBonusType<B extends AlbumBonus> {

    public static final Codec<AlbumBonus> INSTANCE_CODEC = CollectorsAlbumRegistries.ALBUM_BONUS.byNameCodec().dispatch(AlbumBonus::getType, t -> t.codec);

    private final AlbumBonusTarget target;
    private final MapCodec<B> codec;

    public AlbumBonusType(AlbumBonusTarget target, MapCodec<B> codec) {
        this.target = target;
        this.codec = codec;
    }

    public AlbumBonusTarget getTarget() {
        return target;
    }
}
