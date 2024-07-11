package team.tnt.collectorsalbum.common.init;

import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.AlbumCategoryType;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.card.DefaultAlbumCategory;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class CategoryRegistry {

    public static final PlatformRegistry<AlbumCategoryType<?>> REGISTRY = PlatformRegistry.create(CollectorsAlbumRegistries.CATEGORY, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<AlbumCategoryType<DefaultAlbumCategory>> CATEGORY = REGISTRY.register("category", () -> new AlbumCategoryType<>(DefaultAlbumCategory.CODEC));
}
