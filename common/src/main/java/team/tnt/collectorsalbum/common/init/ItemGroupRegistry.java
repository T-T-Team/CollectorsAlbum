package team.tnt.collectorsalbum.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class ItemGroupRegistry {

    public static final PlatformRegistry<CreativeModeTab> REGISTRY = PlatformRegistry.create(BuiltInRegistries.CREATIVE_MODE_TAB, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<CreativeModeTab> ALBUM = REGISTRY.register("album", key -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, -1)
            .icon(() -> ItemRegistry.ALBUM.get().getDefaultInstance())
            .title(Component.translatable(key.toLanguageKey("itemGroup")))
            .displayItems((params, builder) -> {
                //builder.accept(ItemRegistry.TRADING_STATION.get());
                builder.accept(ItemRegistry.ALBUM.get());
                BuiltInRegistries.ITEM.stream()
                        .filter(item -> {
                            Identifier identifier = BuiltInRegistries.ITEM.getKey(item);
                            return identifier.getNamespace().equals(CollectorsAlbum.MOD_ID) && item.getClass().equals(Item.class);
                        })
                        .forEach(builder::accept);
            })
            .build()
    );
}
