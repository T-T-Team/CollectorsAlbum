package team.tnt.collectorsalbum.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.item.AlbumItem;
import team.tnt.collectorsalbum.common.registry.MultiLoaderRegistry;

import java.util.function.Supplier;

public final class ItemRegistry {

    public static final MultiLoaderRegistry<Item> REGISTRY = MultiLoaderRegistry.create(BuiltInRegistries.ITEM, CollectorsAlbum.MOD_ID);

    public static final Supplier<AlbumItem> ALBUM = REGISTRY.register("album", () -> new AlbumItem(new Item.Properties().stacksTo(1)));
}
