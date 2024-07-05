package team.tnt.collectorsalbum.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

import java.util.function.Supplier;

public final class ItemGroupRegistry {

    public static final PlatformRegistry<CreativeModeTab> REGISTRY = PlatformRegistry.create(BuiltInRegistries.CREATIVE_MODE_TAB, CollectorsAlbum.MOD_ID);

    public static final Supplier<CreativeModeTab> ALBUM = REGISTRY.register("album", key -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, -1)
            .icon(() -> ItemRegistry.ALBUM.get().getDefaultInstance())
            .title(Component.translatable(key.toLanguageKey("itemGroup")))
            .displayItems((params, builder) -> {
                BuiltInRegistries.ITEM.stream()
                        .filter(item -> {
                            ResourceLocation identifier = BuiltInRegistries.ITEM.getKey(item);
                            return identifier.getNamespace().equals(CollectorsAlbum.MOD_ID);
                        })
                        .forEach(builder::accept);
            })
            .build()
    );
}
