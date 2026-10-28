package team.tnt.collectorsalbum.common.init;

import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.common.resource.bonus.*;
import team.tnt.collectorsalbum.platform.registration.RegistrationManager;

public final class AlbumBonusRegistry {

    public static void init(RegistrationManager<MapCodec<? extends AlbumBonus>> registry) {
        registry.registerInstance("none", NoBonus.CODEC);
        registry.registerInstance("list", ListAlbumBonus.CODEC);
        registry.registerInstance("album_point_filter", AlbumPointBonusFilter.CODEC);
        registry.registerInstance("first_applicable", FirstApplicableBonus.CODEC);
        registry.registerInstance("category_cards", AlbumCategoryCardBonusFilter.CODEC);
        registry.registerInstance("config_toggle", ConfigToggleBonusFilter.CODEC);

        registry.registerInstance("attribute", AttributeAlbumBonus.CODEC);
        registry.registerInstance("effect", AlbumMobEffectBonus.CODEC);
    }
}
