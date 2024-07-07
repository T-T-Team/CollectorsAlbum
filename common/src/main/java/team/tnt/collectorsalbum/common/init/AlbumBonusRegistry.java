package team.tnt.collectorsalbum.common.init;

import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.resource.bonus.*;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class AlbumBonusRegistry {

    public static final PlatformRegistry<AlbumBonusType<?>> REGISTRY = PlatformRegistry.create(CollectorsAlbumRegistries.ALBUM_BONUS, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<AlbumBonusType<NoBonus>> NONE = REGISTRY.register("none", () -> new AlbumBonusType<>(NoBonus.CODEC));
    public static final PlatformRegistry.Reference<AlbumBonusType<AlbumPointBonusFilter>> ALBUM_POINT_FILTER = REGISTRY.register("album_point_filter", () -> new AlbumBonusType<>(AlbumPointBonusFilter.CODEC));
    public static final PlatformRegistry.Reference<AlbumBonusType<FirstApplicableBonus>> FIRST_APPLICABLE = REGISTRY.register("first_applicable", () -> new AlbumBonusType<>(FirstApplicableBonus.CODEC));
    public static final PlatformRegistry.Reference<AlbumBonusType<AlbumCategoryCardBonusFilter>> CATEGORY_FILTER = REGISTRY.register("category_cards", () -> new AlbumBonusType<>(AlbumCategoryCardBonusFilter.CODEC));
    public static final PlatformRegistry.Reference<AlbumBonusType<ConfigToggleBonusFilter>> CONFIG_TOGGLE = REGISTRY.register("config_toggle", () -> new AlbumBonusType<>(ConfigToggleBonusFilter.CODEC));

    public static final PlatformRegistry.Reference<AlbumBonusType<AttributeAlbumBonus>> ATTRIBUTE = REGISTRY.register("attribute", () -> new AlbumBonusType<>(AttributeAlbumBonus.CODEC));
    public static final PlatformRegistry.Reference<AlbumBonusType<AlbumMobEffectBonus>> MOB_EFFECT = REGISTRY.register("effect", () -> new AlbumBonusType<>(AlbumMobEffectBonus.CODEC));
}
