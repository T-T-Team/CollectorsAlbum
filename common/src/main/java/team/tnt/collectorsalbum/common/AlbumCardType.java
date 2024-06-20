package team.tnt.collectorsalbum.common;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public final class AlbumCardType<C extends AlbumCard> {

    private final ResourceLocation identifier;
    private final MapCodec<C> codec;

    public AlbumCardType(ResourceLocation identifier, MapCodec<C> codec) {
        this.identifier = identifier;
        this.codec = codec;
    }
}
