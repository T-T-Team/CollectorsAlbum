package team.tnt.collectorsalbum.common;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropProviderType;
import team.tnt.collectorsalbum.common.resource.function.NumberProviderType;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistryFactory;

public final class CollectorsAlbumRegistries {

    public static final PlatformRegistry.Reference<AlbumCardType<?>> CARD_TYPE = PlatformRegistryFactory.createSimple(Keys.CARD_TYPE_KEY);
    public static final PlatformRegistry.Reference<AlbumCategoryType<?>> CATEGORY = PlatformRegistryFactory.createSimple(Keys.CARD_CATEGORY_KEY);
    public static final PlatformRegistry.Reference<NumberProviderType<?>> NUMBER_PROVIDER = PlatformRegistryFactory.createSimple(Keys.NUMBER_PROVIDER);
    public static final PlatformRegistry.Reference<ItemDropProviderType<?>> ITEM_DROP_PROVIDER = PlatformRegistryFactory.createSimple(Keys.ITEM_DROP_PROVIDER);

    public static final class Keys {

        public static final ResourceKey<Registry<AlbumCardType<?>>> CARD_TYPE_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "card_type"));
        public static final ResourceKey<Registry<AlbumCategoryType<?>>> CARD_CATEGORY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "category"));
        public static final ResourceKey<Registry<NumberProviderType<?>>> NUMBER_PROVIDER = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "number_provider"));
        public static final ResourceKey<Registry<ItemDropProviderType<?>>> ITEM_DROP_PROVIDER = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "item_drop_provider"));
    }
}
