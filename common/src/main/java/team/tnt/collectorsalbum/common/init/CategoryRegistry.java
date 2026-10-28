package team.tnt.collectorsalbum.common.init;

import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.card.DefaultAlbumCategory;
import team.tnt.collectorsalbum.platform.registration.RegistrationManager;

@Deprecated // TODO have single category type
public final class CategoryRegistry {

    public static void init(RegistrationManager<MapCodec<? extends AlbumCategory>> registry) {
        registry.registerInstance("category", DefaultAlbumCategory.CODEC);
    }
}
