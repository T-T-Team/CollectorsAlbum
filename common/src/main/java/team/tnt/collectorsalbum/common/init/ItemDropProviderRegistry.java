package team.tnt.collectorsalbum.common.init;

import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.resource.drops.*;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class ItemDropProviderRegistry {

    public static final PlatformRegistry<ItemDropProviderType<?>> REGISTRY = PlatformRegistry.create(CollectorsAlbumRegistries.ITEM_DROP_PROVIDER, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<ItemDropProviderType<NoItemDropProvider>> NO_DROP_PROVIDER = REGISTRY.register("none", () -> new ItemDropProviderType<>(NoItemDropProvider.CODEC));
    public static final PlatformRegistry.Reference<ItemDropProviderType<ItemStackDropProvider>> ITEMSTACK_DROP_PROVIDER = REGISTRY.register("item", () -> new ItemDropProviderType<>(ItemStackDropProvider.CODEC));
    public static final PlatformRegistry.Reference<ItemDropProviderType<TagDropProvider>> TAG_DROP_PROVIDER = REGISTRY.register("tag", () -> new ItemDropProviderType<>(TagDropProvider.CODEC));
    public static final PlatformRegistry.Reference<ItemDropProviderType<RepeatedItemDropProvider>> REPEATED_DROP_PROVIDER = REGISTRY.register("repeated", () -> new ItemDropProviderType<>(RepeatedItemDropProvider.CODEC));
    public static final PlatformRegistry.Reference<ItemDropProviderType<WeightedItemDropProvider>> WEIGHTED_DROP_PROVIDER = REGISTRY.register("weighted_select", () -> new ItemDropProviderType<>(WeightedItemDropProvider.CODEC));
    public static final PlatformRegistry.Reference<ItemDropProviderType<ListItemDropProvider>> LIST_DROP_PROVIDER = REGISTRY.register("list", () -> new ItemDropProviderType<>(ListItemDropProvider.CODEC));
    public static final PlatformRegistry.Reference<ItemDropProviderType<EntityFilterItemDropProvider>> ENTITY_FILTER = REGISTRY.register("entity_filter", () -> new ItemDropProviderType<>(EntityFilterItemDropProvider.CODEC));
    public static final PlatformRegistry.Reference<ItemDropProviderType<RandomChanceFilterItemDropProvider>> RANDOM_CHANCE = REGISTRY.register("random_chance", () -> new ItemDropProviderType<>(RandomChanceFilterItemDropProvider.CODEC));
    public static final PlatformRegistry.Reference<ItemDropProviderType<DropReferenceProvider>> REFERENCE = REGISTRY.register("reference", () -> new ItemDropProviderType<>(DropReferenceProvider.CODEC));
}
