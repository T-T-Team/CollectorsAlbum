package team.tnt.collectorsalbum.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class MenuRegistry {

    public static final PlatformRegistry<MenuType<?>> REGISTRY = PlatformRegistry.create(BuiltInRegistries.MENU, CollectorsAlbum.MOD_ID);
    private static final PlatformRegistry.MenuHelper HELPER = PlatformRegistry.createMenuHelper(REGISTRY);

    public static final PlatformRegistry.Reference<MenuType<AlbumCategoryMenu>> ALBUM_CATEGORY = HELPER.register("album_category", AlbumCategoryMenu::new, ResourceLocation.STREAM_CODEC);
}
