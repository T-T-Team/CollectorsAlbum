package team.tnt.collectorsalbum.common.init;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

import java.util.List;
import java.util.function.Supplier;

public final class ItemDataComponentRegistry {

    public static final PlatformRegistry<DataComponentType<?>> REGISTRY = PlatformRegistry.create(BuiltInRegistries.DATA_COMPONENT_TYPE, CollectorsAlbum.MOD_ID);

    public static final Supplier<DataComponentType<List<ItemStack>>> PACK_DROPS = REGISTRY.register("pack_drops",
            () -> DataComponentType.<List<ItemStack>>builder()
                    .persistent(ItemStack.CODEC.listOf())
                    .networkSynchronized(ItemStack.LIST_STREAM_CODEC)
                    .build()
    );

    public static final Supplier<DataComponentType<Album>> ALBUM = REGISTRY.register("album",
            () -> DataComponentType.<Album>builder()
                    .persistent(Album.CODEC)
                    .networkSynchronized(Album.STREAM_CODEC)
                    .build()
    );
}
