package team.tnt.collectorsalbum.common;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.init.RegistryTags;
import team.tnt.collectorsalbum.integrations.PlatformIntegrations;

import java.util.*;

public final class PlayerAlbumTracker {

    private static final PlayerAlbumTracker INSTANCE = new PlayerAlbumTracker();
    private final Map<UUID, Album> playerAlbums = new HashMap<>();

    private PlayerAlbumTracker() {}

    public static PlayerAlbumTracker get() {
        return INSTANCE;
    }

    public AlbumLocatorResult findAlbum(Player player, Album previousAlbum) {
        AlbumLocatorResult integrationResult = PlatformIntegrations.getAlbumLocatorResult(player, previousAlbum);
        if (integrationResult.exists()) {
            return integrationResult;
        }
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (!itemStack.isEmpty() && itemStack.is(RegistryTags.Items.ALBUM)) {
                Album album = itemStack.get(ItemDataComponentRegistry.ALBUM.get());
                if (previousAlbum != null && previousAlbum.test(album)) {
                    return AlbumLocatorResult.found(itemStack, previousAlbum, i);
                }
                return AlbumLocatorResult.found(itemStack, album, i);
            }
        }
        return AlbumLocatorResult.notFound();
    }

    public Optional<Album> getAlbum(Player player) {
        return this.getAlbum(player.getUUID());
    }

    public Optional<Album> getAlbum(UUID playerUuid) {
        return Optional.ofNullable(this.playerAlbums.get(playerUuid));
    }

    public void cacheAlbum(Player player, Album album) {
        this.cacheAlbum(player.getUUID(), album);
    }

    public void cacheAlbum(UUID playerUuid, Album album) {
        this.playerAlbums.put(playerUuid, Objects.requireNonNull(album));
    }

    public void deleteCachedAlbum(UUID playerUuid) {
        this.playerAlbums.remove(playerUuid);
    }

    public void clearCache() {
        this.playerAlbums.clear();
    }
}
