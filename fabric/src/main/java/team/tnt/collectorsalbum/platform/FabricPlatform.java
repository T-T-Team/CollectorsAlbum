package team.tnt.collectorsalbum.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import team.tnt.collectorsalbum.client.screen.AlbumNavigationHelper;

public class FabricPlatform implements Platform {

    public static MinecraftServer server;

    @Override
    public Side getSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? Side.CLIENT : Side.SERVER;
    }

    @Override
    public boolean isModLoaded(String namespace) {
        return FabricLoader.getInstance().isModLoaded(namespace);
    }

    @Override
    public MinecraftServer getServerInstance() {
        return server;
    }

    @Override
    public <T> void openMenu(ServerPlayer player, StreamCodec<? super FriendlyByteBuf, T> codec, PlatformMenuProvider<T> provider) {
        player.openMenu(new ExtendedScreenHandlerFactory<>() {
            @Override
            public Object getScreenOpeningData(ServerPlayer player) {
                return provider.getMenuData(player);
            }

            @Override
            public Component getDisplayName() {
                return provider.getTitle();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return provider.createMenu(i, inventory, player);
            }
        });
    }

    @Override
    public <M extends AbstractContainerMenu, D> MenuType<M> createMenu(MenuFactory<M, D> factory, StreamCodec<? super FriendlyByteBuf, D> dataCodec) {
        return new ExtendedScreenHandlerType<>(factory::createMenu, dataCodec);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void openAlbumUi(ItemStack itemStack) {
        AlbumNavigationHelper.storeItemStack(itemStack);
        AlbumNavigationHelper.navigateHomepage();
    }
}
