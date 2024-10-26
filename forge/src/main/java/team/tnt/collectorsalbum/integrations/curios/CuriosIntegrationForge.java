package team.tnt.collectorsalbum.integrations.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumLocatorResult;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;

public final class CuriosIntegrationForge {

    public static AlbumLocatorResult find(Player player, Album previousAlbum) {
        return CuriosApi.getCuriosInventory(player).map(itemHandler -> {
            Optional<ICurioStacksHandler> stacksHandler = itemHandler.getStacksHandler("album");
            return stacksHandler.map(handler -> {
                IDynamicStackHandler stackHandler = handler.getStacks();
                for (int i = 0; i < stackHandler.getSlots(); i++) {
                    ItemStack itemStack = stackHandler.getStackInSlot(i);
                    if (!itemStack.isEmpty()) {
                        Album album = Album.get(itemStack);
                        if (previousAlbum != null && previousAlbum.test(album)) {
                            return AlbumLocatorResult.found(itemStack, previousAlbum, i);
                        }
                        if (album != null) {
                            return AlbumLocatorResult.found(itemStack, album, i);
                        }
                    }
                }
                return AlbumLocatorResult.notFound();
            }).orElseGet(AlbumLocatorResult::notFound);
        }).orElseGet(AlbumLocatorResult::notFound);
    }
}
