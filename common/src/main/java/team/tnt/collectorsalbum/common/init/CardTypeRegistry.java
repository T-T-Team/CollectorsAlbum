package team.tnt.collectorsalbum.common.init;

import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.card.AlbumCardType;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.card.RarityCard;
import team.tnt.collectorsalbum.common.card.SimpleCard;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class CardTypeRegistry {

    public static final PlatformRegistry<AlbumCardType<?>> REGISTRY = PlatformRegistry.create(CollectorsAlbumRegistries.CARD_TYPE, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<AlbumCardType<SimpleCard>> CARD = REGISTRY.register("card", key -> new AlbumCardType<>(key, SimpleCard.CODEC));
    public static final PlatformRegistry.Reference<AlbumCardType<RarityCard>> RARITY_CARD = REGISTRY.register("rarity_card", key -> new AlbumCardType<>(key, RarityCard.CODEC));
}
