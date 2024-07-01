package team.tnt.collectorsalbum.common;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public final class AlbumCategoryType<C extends AlbumCategory> {

    private final MapCodec<C> codec;

    public AlbumCategoryType(MapCodec<C> codec) {
        this.codec = codec;
    }
}
