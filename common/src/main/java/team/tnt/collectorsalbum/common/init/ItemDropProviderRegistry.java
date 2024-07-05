package team.tnt.collectorsalbum.common.init;

import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.resource.drops.*;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

import java.util.function.Supplier;

public final class ItemDropProviderRegistry {

    public static final PlatformRegistry<ItemDropProviderType<?>> REGISTRY = PlatformRegistry.create(CollectorsAlbumRegistries.ITEM_DROP_PROVIDER, CollectorsAlbum.MOD_ID);

    public static final Supplier<ItemDropProviderType<NoItemDropProvider>> NO_DROP_PROVIDER = REGISTRY.register("none", () -> new ItemDropProviderType<>(NoItemDropProvider.CODEC));
    public static final Supplier<ItemDropProviderType<ItemStackDropProvider>> ITEMSTACK_DROP_PROVIDER = REGISTRY.register("item", () -> new ItemDropProviderType<>(ItemStackDropProvider.CODEC));
    public static final Supplier<ItemDropProviderType<TagDropProvider>> TAG_DROP_PROVIDER = REGISTRY.register("tag", () -> new ItemDropProviderType<>(TagDropProvider.CODEC));
    public static final Supplier<ItemDropProviderType<RepeatedItemDropProvider>> REPEATED_DROP_PROVIDER = REGISTRY.register("repeated", () -> new ItemDropProviderType<>(RepeatedItemDropProvider.CODEC));
    public static final Supplier<ItemDropProviderType<WeightedItemDropProvider>> WEIGHTED_DROP_PROVIDER = REGISTRY.register("weighted_select", () -> new ItemDropProviderType<>(WeightedItemDropProvider.CODEC));
    public static final Supplier<ItemDropProviderType<ListItemDropProvider>> LIST_DROP_PROVIDER = REGISTRY.register("list", () -> new ItemDropProviderType<>(ListItemDropProvider.CODEC));
}
