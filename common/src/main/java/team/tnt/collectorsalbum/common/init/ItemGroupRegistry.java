package team.tnt.collectorsalbum.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

import java.util.function.Supplier;

public final class ItemGroupRegistry {

    public static final PlatformRegistry<CreativeModeTab> REGISTRY = PlatformRegistry.create(BuiltInRegistries.CREATIVE_MODE_TAB, CollectorsAlbum.MOD_ID);

    public static final Supplier<CreativeModeTab> ALBUM = REGISTRY.register("album", key -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, -1)
            .icon(() -> ItemRegistry.ALBUM.get().getDefaultInstance())
            .title(Component.translatable(key.toLanguageKey("itemGroup")))
            .displayItems((params, builder) -> {
                builder.accept(ItemRegistry.TRADING_STATION.get());
                builder.accept(ItemRegistry.ALBUM.get());
                builder.accept(ItemRegistry.COMMON_CARD_PACK.get());
                builder.accept(ItemRegistry.UNCOMMON_CARD_PACK.get());
                builder.accept(ItemRegistry.RARE_CARD_PACK.get());
                builder.accept(ItemRegistry.EPIC_CARD_PACK.get());
                builder.accept(ItemRegistry.LEGENDARY_CARD_PACK.get());
                builder.accept(ItemRegistry.MYTHICAL_CARD_PACK.get());
                builder.accept(ItemRegistry.COMMON_REPACKED_CARD_PACK.get());
                builder.accept(ItemRegistry.UNCOMMON_REPACKED_CARD_PACK.get());
                builder.accept(ItemRegistry.RARE_REPACKED_CARD_PACK.get());
                builder.accept(ItemRegistry.EPIC_REPACKED_CARD_PACK.get());
                builder.accept(ItemRegistry.LEGENDARY_REPACKED_CARD_PACK.get());
                builder.accept(ItemRegistry.MYTHICAL_REPACKED_CARD_PACK.get());
                BuiltInRegistries.ITEM.stream()
                        .filter(item -> {
                            ResourceLocation identifier = BuiltInRegistries.ITEM.getKey(item);
                            return identifier.getNamespace().equals(CollectorsAlbum.MOD_ID) && item.getClass().equals(Item.class);
                        })
                        .forEach(builder::accept);
                builder.accept(ItemRegistry.COMMON_CUSTOM_CARD_PACK.get());
                builder.accept(ItemRegistry.UNCOMMON_CUSTOM_CARD_PACK.get());
                builder.accept(ItemRegistry.RARE_CUSTOM_CARD_PACK.get());
                builder.accept(ItemRegistry.EPIC_CUSTOM_CARD_PACK.get());
                builder.accept(ItemRegistry.LEGENDARY_CUSTOM_CARD_PACK.get());
                builder.accept(ItemRegistry.MYTHICAL_CUSTOM_CARD_PACK.get());
            })
            .build()
    );
}
