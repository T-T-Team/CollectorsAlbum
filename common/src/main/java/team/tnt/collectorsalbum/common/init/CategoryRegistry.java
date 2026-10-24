package team.tnt.collectorsalbum.common.init;

import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.card.DefaultAlbumCategory;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class CategoryRegistry {

    public static final PlatformRegistry<MapCodec<? extends AlbumCategory>> REGISTRY = PlatformRegistry.create(CollectorsAlbumRegistries.CATEGORY, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<MapCodec<DefaultAlbumCategory>> CATEGORY = REGISTRY.registerInstance("category", DefaultAlbumCategory.CODEC);
}
