package team.tnt.collectorsalbum.common.init;

import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.AlbumCardType;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.card.SimpleAlbumCard;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

import java.util.function.Supplier;

public final class CardTypeRegistry {

    public static final PlatformRegistry<AlbumCardType<?>> REGISTRY = PlatformRegistry.create(CollectorsAlbumRegistries.CARD_TYPE, CollectorsAlbum.MOD_ID);

    public static final Supplier<AlbumCardType<SimpleAlbumCard>> CARD = REGISTRY.register("card", key -> new AlbumCardType<>(key, SimpleAlbumCard.CODEC));
}
