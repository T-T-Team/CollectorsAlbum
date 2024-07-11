package team.tnt.collectorsalbum.common.init;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

import java.util.List;

public final class ItemDataComponentRegistry {

    public static final PlatformRegistry<DataComponentType<?>> REGISTRY = PlatformRegistry.create(BuiltInRegistries.DATA_COMPONENT_TYPE, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<DataComponentType<List<ItemStack>>> PACK_DROPS = REGISTRY.register("pack_drops",
            () -> DataComponentType.<List<ItemStack>>builder()
                    .persistent(ItemStack.CODEC.listOf())
                    .networkSynchronized(ItemStack.LIST_STREAM_CODEC)
                    .build()
    );

    public static final PlatformRegistry.Reference<DataComponentType<Album>> ALBUM = REGISTRY.register("album",
            () -> DataComponentType.<Album>builder()
                    .persistent(Album.CODEC)
                    .build()
    );

    public static final PlatformRegistry.Reference<DataComponentType<ResourceLocation>> PACK_DROPS_TABLE = REGISTRY.register("pack_drops_table",
            () -> DataComponentType.<ResourceLocation>builder()
                    .persistent(ResourceLocation.CODEC)
                    .networkSynchronized(ResourceLocation.STREAM_CODEC)
                    .build()
    );
}
