package team.tnt.collectorsalbum.common.init;

import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.resource.bonus.*;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class AlbumBonusRegistry {

    public static final PlatformRegistry<MapCodec<? extends AlbumBonus>> REGISTRY = PlatformRegistry.create(CollectorsAlbumRegistries.ALBUM_BONUS, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<MapCodec<NoBonus>> NONE = REGISTRY.registerInstance("none", NoBonus.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<ListAlbumBonus>> LIST = REGISTRY.registerInstance("list", ListAlbumBonus.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<AlbumPointBonusFilter>> ALBUM_POINT_FILTER = REGISTRY.registerInstance("album_point_filter", AlbumPointBonusFilter.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<FirstApplicableBonus>> FIRST_APPLICABLE = REGISTRY.registerInstance("first_applicable", FirstApplicableBonus.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<AlbumCategoryCardBonusFilter>> CATEGORY_FILTER = REGISTRY.registerInstance("category_cards", AlbumCategoryCardBonusFilter.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<ConfigToggleBonusFilter>> CONFIG_TOGGLE = REGISTRY.registerInstance("config_toggle", ConfigToggleBonusFilter.CODEC);

    public static final PlatformRegistry.Reference<MapCodec<AttributeAlbumBonus>> ATTRIBUTE = REGISTRY.registerInstance("attribute", AttributeAlbumBonus.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<AlbumMobEffectBonus>> MOB_EFFECT = REGISTRY.registerInstance("effect", AlbumMobEffectBonus.CODEC);
}
