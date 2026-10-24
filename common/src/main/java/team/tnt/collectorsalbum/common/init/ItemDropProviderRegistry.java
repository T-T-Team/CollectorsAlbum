package team.tnt.collectorsalbum.common.init;

import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.resource.drops.*;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class ItemDropProviderRegistry {

    public static final PlatformRegistry<MapCodec<? extends ItemDropProvider>> REGISTRY = PlatformRegistry.create(CollectorsAlbumRegistries.ITEM_DROP_PROVIDER, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<MapCodec<NoItemDropProvider>> NO_DROP_PROVIDER = REGISTRY.registerInstance("none", NoItemDropProvider.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<ItemStackDropProvider>> ITEMSTACK_DROP_PROVIDER = REGISTRY.registerInstance("item", ItemStackDropProvider.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<TagDropProvider>> TAG_DROP_PROVIDER = REGISTRY.registerInstance("tag", TagDropProvider.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<RepeatedItemDropProvider>> REPEATED_DROP_PROVIDER = REGISTRY.registerInstance("repeated", RepeatedItemDropProvider.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<WeightedItemDropProvider>> WEIGHTED_DROP_PROVIDER = REGISTRY.registerInstance("weighted_select", WeightedItemDropProvider.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<ListItemDropProvider>> LIST_DROP_PROVIDER = REGISTRY.registerInstance("list", ListItemDropProvider.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<RandomListItemDropProvider>> RANDOM_ITEM_DROP_PROVIDER = REGISTRY.registerInstance("random_item", RandomListItemDropProvider.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<EntityFilterItemDropProvider>> ENTITY_FILTER = REGISTRY.registerInstance("entity_filter", EntityFilterItemDropProvider.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<MonsterEntityFilterItemDropProvider>> MONSTER_ENTITY_FILTER = REGISTRY.registerInstance("monster_filter", MonsterEntityFilterItemDropProvider.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<RandomChanceFilterItemDropProvider>> RANDOM_CHANCE = REGISTRY.registerInstance("random_chance", RandomChanceFilterItemDropProvider.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<DropReferenceProvider>> REFERENCE = REGISTRY.registerInstance("reference", DropReferenceProvider.CODEC);
}
