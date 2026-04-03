package team.tnt.collectorsalbum.common.tracking;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.init.RegistryTags;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public final class PlayerAlbumTracker {

    private static final Marker MARKER = MarkerManager.getMarker("AlbumTracker");
    private static final PlayerAlbumTracker INSTANCE = new PlayerAlbumTracker();
    public static final AlbumFinder VANILLA = new AlbumFinder(
            Identifier.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "vanilla"),
            INSTANCE::findAlbum,
            INSTANCE::getItem
    );

    private final Map<Identifier, AlbumFinder> finders = new LinkedHashMap<>();
    private final Map<UUID, CachedAlbum> playerAlbums = new HashMap<>();

    private PlayerAlbumTracker() {
    }

    public static PlayerAlbumTracker get() {
        return INSTANCE;
    }

    public void registerFinder(AlbumFinder finder) {
        if (this.finders.put(finder.id(), finder) != null) {
            throw new IllegalArgumentException("Duplicate album finder: " + finder.id());
        }
    }

    public void update(Player player) {
        // check existing cache
        CachedAlbum cachedAlbum = this.playerAlbums.get(player.getUUID());
        boolean invalidated = false;
        if (cachedAlbum != null) {
            ItemStack albumStack = cachedAlbum.key().getItem(player);
            if (cachedAlbum.is(albumStack)) {
                cachedAlbum.value().tick(player);
                return;
            } else {
                invalidated = true;
            }
        }
        CachedAlbum foundAlbum = this.callFinderFunction(
                finder -> finder.loader().find(player, slot -> new InventoryKey(finder, slot)),
                Objects::nonNull,
                null
        );
        if (foundAlbum == null) {
            this.deleteCachedAlbum(player);
            if (invalidated) {
                CollectorsAlbum.LOGGER.debug(MARKER, "Removing album effects for invalidated album {} for player {}", cachedAlbum, player.getUUID());
                cachedAlbum.value().removed(player);
            }
            return;
        }
        if (invalidated) {
            if (!cachedAlbum.is(foundAlbum.key().getItem(player))) {
                CollectorsAlbum.LOGGER.debug(MARKER, "Removing album effects for invalidated album {} for player {} with replacement album {}", cachedAlbum, player.getUUID(), foundAlbum);
                cachedAlbum.value().removed(player);
            }
            CollectorsAlbum.LOGGER.debug(MARKER, "Updating moved album {} for player {}", foundAlbum, player.getUUID());
        }
        foundAlbum.value().tick(player);
        this.cacheAlbum(player, foundAlbum);
    }

    private void cacheAlbum(Player player, CachedAlbum album) {
        this.playerAlbums.put(player.getUUID(), Objects.requireNonNull(album));
        CollectorsAlbum.LOGGER.debug(MARKER, "Cached album for player {}: {}", player.getUUID(), album);
    }

    public void deleteCachedAlbum(Player player) {
        if (!this.playerAlbums.containsKey(player.getUUID())) {
            return;
        }
        CachedAlbum album = this.playerAlbums.remove(player.getUUID());
        CollectorsAlbum.LOGGER.debug(MARKER, "Deleted cached album for player {}: {}", player.getUUID(), album);
    }

    public void clearCache() {
        CollectorsAlbum.LOGGER.debug(MARKER, "Clearing album cache");
        this.playerAlbums.clear();
    }

    private <T> T callFinderFunction(Function<AlbumFinder, T> function, Predicate<T> validator, T fallback) {
        for (AlbumFinder finder : this.finders.values()) {
            T result = function.apply(finder);
            if (validator.test(result)) {
                return result;
            }
        }
        return fallback;
    }

    private CachedAlbum findAlbum(Player player, IntFunction<InventoryKey> keyFactory) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (!itemStack.isEmpty() && itemStack.is(RegistryTags.Items.ALBUM) && itemStack.has(ItemDataComponentRegistry.ALBUM.get())) {
                return new CachedAlbum(keyFactory.apply(i), itemStack.get(ItemDataComponentRegistry.ALBUM.get()));
            }
        }
        return null;
    }

    private ItemStack getItem(Player player, int slot) {
        return player.getInventory().getItem(slot);
    }

}
