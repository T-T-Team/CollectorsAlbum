package team.tnt.collectorsalbum.common.init;

import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.common.resource.drops.*;
import team.tnt.collectorsalbum.platform.registration.RegistrationManager;

public final class ItemDropProviderRegistry {

    public static void init(RegistrationManager<MapCodec<? extends ItemDropProvider>> registry) {
        registry.registerInstance("none", NoItemDropProvider.CODEC);
        registry.registerInstance("item", ItemStackDropProvider.CODEC);
        registry.registerInstance("tag", TagDropProvider.CODEC);
        registry.registerInstance("repeated", RepeatedItemDropProvider.CODEC);
        registry.registerInstance("weighted_select", WeightedItemDropProvider.CODEC);
        registry.registerInstance("list", ListItemDropProvider.CODEC);
        registry.registerInstance("random_item", RandomListItemDropProvider.CODEC);
        registry.registerInstance("entity_filter", EntityFilterItemDropProvider.CODEC);
        registry.registerInstance("monster_filter", MonsterEntityFilterItemDropProvider.CODEC);
        registry.registerInstance("random_chance", RandomChanceFilterItemDropProvider.CODEC);
        registry.registerInstance("reference", DropReferenceProvider.CODEC);
    }
}
