package team.tnt.collectorsalbum.integrations.trinkets;

import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
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

import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;

public class TrinketPlugin implements StartupPlugin {

    private static final TrinketPlugin INSTANCE = new TrinketPlugin();
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "trinkets");
    private static final AlbumFinder FINDER = new AlbumFinder(
            ID, TrinketPlugin::loadAlbum, TrinketPlugin::getItem
    );
    private static final String COMPONENT = "hand";
    private static final String INVENTORY = "album";


    private TrinketPlugin() {
    }

    public static TrinketPlugin instance() {
        return INSTANCE;
    }

    @Override
    public void onStartup() {
        PlayerAlbumTracker tracker = PlayerAlbumTracker.get();
        tracker.registerFinder(FINDER);
    }

    private static CachedAlbum loadAlbum(Player player, IntFunction<InventoryKey> factory) {
        return getInventory(player).map(inventory -> {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack itemStack = inventory.getItem(i);
                if (!itemStack.isEmpty() && itemStack.has(ItemDataComponentRegistry.ALBUM.get())) {
                    return new CachedAlbum(factory.apply(i), itemStack.get(ItemDataComponentRegistry.ALBUM.get()));
                }
            }
            return null;
        }).orElse(null);
    }

    private static ItemStack getItem(Player player, int slot) {
        return getInventory(player).map(inventory -> {
            if (slot >= 0 && slot < inventory.getContainerSize()) {
                return inventory.getItem(slot);
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);
    }

    private static Optional<TrinketInventory> getInventory(Player player) {
        return TrinketsApi.getTrinketComponent(player).map(component -> {
            Map<String, TrinketInventory> inventory = component.getInventory().get(COMPONENT);
            if (inventory == null)
                return null;
            return inventory.get(INVENTORY);
        });
    }
}
