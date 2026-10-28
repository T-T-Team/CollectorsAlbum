package team.tnt.collectorsalbum.common.init;

import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.card.RarityCard;
import team.tnt.collectorsalbum.common.card.SimpleCard;
import team.tnt.collectorsalbum.platform.registration.RegistrationManager;

@Deprecated
public final class CardTypeRegistry {

    public static void init(RegistrationManager<MapCodec<? extends AlbumCard>> registry) {
        registry.registerInstance("card", SimpleCard.CODEC);
        registry.registerInstance("rarity_card", RarityCard.CODEC);
    }
}
