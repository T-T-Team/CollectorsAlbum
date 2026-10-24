package team.tnt.collectorsalbum.common;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.resource.bonus.AlbumBonus;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistryFactory;

public final class CollectorsAlbumRegistries {

    public static final PlatformRegistry.RegistryReference<MapCodec<? extends AlbumCard>> CARD_TYPE = PlatformRegistryFactory.createSimple(Keys.CARD_TYPE_KEY);
    public static final PlatformRegistry.RegistryReference<MapCodec<? extends AlbumCategory>> CATEGORY = PlatformRegistryFactory.createSimple(Keys.CARD_CATEGORY_KEY);
    public static final PlatformRegistry.RegistryReference<MapCodec<? extends NumberProvider>> NUMBER_PROVIDER = PlatformRegistryFactory.createSimple(Keys.NUMBER_PROVIDER);
    public static final PlatformRegistry.RegistryReference<MapCodec<? extends ItemDropProvider>> ITEM_DROP_PROVIDER = PlatformRegistryFactory.createSimple(Keys.ITEM_DROP_PROVIDER);
    public static final PlatformRegistry.RegistryReference<MapCodec<? extends AlbumBonus>> ALBUM_BONUS = PlatformRegistryFactory.createSimple(Keys.ALBUM_BONUS);

    public static final class Keys {

        public static final ResourceKey<Registry<MapCodec<? extends AlbumCard>>> CARD_TYPE_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "card_type"));
        public static final ResourceKey<Registry<MapCodec<? extends AlbumCategory>>> CARD_CATEGORY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "category"));
        public static final ResourceKey<Registry<MapCodec<? extends NumberProvider>>> NUMBER_PROVIDER = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "number_provider"));
        public static final ResourceKey<Registry<MapCodec<? extends ItemDropProvider>>> ITEM_DROP_PROVIDER = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "item_drop_provider"));
        public static final ResourceKey<Registry<MapCodec<? extends AlbumBonus>>> ALBUM_BONUS = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "album_bonus"));
    }
}
