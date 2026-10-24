package team.tnt.collectorsalbum.common.init;

import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.card.RarityCard;
import team.tnt.collectorsalbum.common.card.SimpleCard;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class CardTypeRegistry {

    public static final PlatformRegistry<MapCodec<? extends AlbumCard>> REGISTRY = PlatformRegistry.create(CollectorsAlbumRegistries.CARD_TYPE, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<MapCodec<SimpleCard>> CARD = REGISTRY.registerInstance("card", SimpleCard.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<RarityCard>> RARITY_CARD = REGISTRY.registerInstance("rarity_card", RarityCard.CODEC);
}
