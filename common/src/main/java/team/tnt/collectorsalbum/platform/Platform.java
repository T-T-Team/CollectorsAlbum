package team.tnt.collectorsalbum.platform;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.platform.network.NetworkCodec;

public interface Platform {

    Platform INSTANCE = JavaServiceLoader.loadService(Platform.class);

    Side getSide();

    boolean isModLoaded(String namespace);

    MinecraftServer getServerInstance();

    <T> void openMenu(ServerPlayer player, NetworkCodec<T> codec, PlatformMenuProvider<T> provider);

    <M extends AbstractContainerMenu, D> MenuType<M> createMenu(MenuFactory<M, D> factory, NetworkCodec<D> codec);

    void openAlbumUi(ItemStack itemStack);

    @FunctionalInterface
    interface MenuFactory<M extends AbstractContainerMenu, D> {
        M createMenu(int menuId, Inventory inventory, D d);
    }

    interface PlatformMenuProvider<D> {
        D getMenuData(ServerPlayer player);
        Component getTitle();
        AbstractContainerMenu createMenu(int menuId, Inventory inventory, Player player);
    }
}
