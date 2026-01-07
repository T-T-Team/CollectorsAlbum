package team.tnt.collectorsalbum.integrations.curios;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.tracking.AlbumFinder;
import team.tnt.collectorsalbum.common.tracking.CachedAlbum;
import team.tnt.collectorsalbum.common.tracking.InventoryKey;
import team.tnt.collectorsalbum.common.tracking.PlayerAlbumTracker;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.integrations.StartupPlugin;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;
import java.util.function.IntFunction;

public class CuriosPlugin implements StartupPlugin {

    private static final CuriosPlugin INSTANCE = new CuriosPlugin();
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "curios");
    private static final String ALBUM_INVENTORY = "album";
    private static final AlbumFinder FINDER = new AlbumFinder(
            ID, CuriosPlugin::loadAlbum, CuriosPlugin::getItem
    );

    private CuriosPlugin() {
    }

    public static CuriosPlugin instance() {
        return INSTANCE;
    }

    @Override
    public void onStartup() {
        PlayerAlbumTracker tracker = PlayerAlbumTracker.get();
        tracker.registerFinder(FINDER);
    }

    private static CachedAlbum loadAlbum(Player player, IntFunction<InventoryKey> keyFactory) {
        return getStackHandler(player).map(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack itemStack = handler.getStackInSlot(i);
                if (!itemStack.isEmpty() && itemStack.has(ItemDataComponentRegistry.ALBUM.get())) {
                    return new CachedAlbum(keyFactory.apply(i), itemStack.get(ItemDataComponentRegistry.ALBUM.get()));
                }
            }
            return null;
        }).orElse(null);
    }

    private static ItemStack getItem(Player player, int slot) {
        return getStackHandler(player).map(handler -> handler.getStackInSlot(slot))
                .orElse(ItemStack.EMPTY);
    }

    private static Optional<IDynamicStackHandler> getStackHandler(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.getStacksHandler(ALBUM_INVENTORY))
                .map(ICurioStacksHandler::getStacks);
    }
}
