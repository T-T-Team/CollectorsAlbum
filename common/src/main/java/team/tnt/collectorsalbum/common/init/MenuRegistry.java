package team.tnt.collectorsalbum.common.init;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;
import team.tnt.collectorsalbum.platform.Platform;
import team.tnt.collectorsalbum.platform.registration.Reference;
import team.tnt.collectorsalbum.platform.registration.RegistrationManager;

public final class MenuRegistry {

    public static Reference<MenuType<AlbumCategoryMenu>> ALBUM_CATEGORY;

    public static void init(RegistrationManager<MenuType<?>> registry) {
        ALBUM_CATEGORY = register(registry, "album_category", AlbumCategoryMenu::new, CollectorsAlbum.ALBUM_PAGE_STREAM_CODEC);
    }

    private static <T extends AbstractContainerMenu, D> Reference<MenuType<T>> register(RegistrationManager<MenuType<?>> registry, String name, Platform.MenuFactory<T, D> factory, StreamCodec<? super FriendlyByteBuf, D> codec) {
        return registry.register(name, _ -> Platform.INSTANCE.createMenu(factory, codec));
    }
}
