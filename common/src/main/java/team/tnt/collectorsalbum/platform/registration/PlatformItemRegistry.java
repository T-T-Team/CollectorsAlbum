package team.tnt.collectorsalbum.platform.registration;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public final class PlatformItemRegistry extends PlatformRegistryImpl<Item> {

    PlatformItemRegistry(String namespace) {
        super(() -> BuiltInRegistries.ITEM, namespace);
    }

    public <T extends Item> Reference<T> registerItem(String elementId, Function<Item.Properties, T> itemFactory) {
        return register(elementId, key -> {
            Item.Properties properties = new Item.Properties();
            properties.setId(ResourceKey.create(Registries.ITEM, key));
            return itemFactory.apply(properties);
        });
    }
}
