package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;

public final class AlbumCardType<C extends AlbumCard> {

    public static final Codec<AlbumCard> INSTANCE_CODEC = CollectorsAlbumRegistries.CARD_TYPE.byNameCodec().dispatch("type", AlbumCard::getType, AlbumCardType::codec);

    private final ResourceLocation identifier;
    private final MapCodec<C> codec;

    public AlbumCardType(ResourceLocation identifier, MapCodec<C> codec) {
        this.identifier = identifier;
        this.codec = codec;
    }

    public ResourceLocation identifier() {
        return this.identifier;
    }

    public MapCodec<C> codec() {
        return this.codec;
    }
}
