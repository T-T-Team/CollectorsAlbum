package team.tnt.collectorsalbum.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class PlayerAlbumTracker {

    private static final PlayerAlbumTracker INSTANCE = new PlayerAlbumTracker();
    private final Map<UUID, Album> playerAlbums = new HashMap<>();

    private PlayerAlbumTracker() {}

    public static PlayerAlbumTracker get() {
        return INSTANCE;
    }

    public Optional<AlbumLocateResult> findAlbum(Player player) {
        return Optional.empty(); // TODO implement
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

    public record AlbumLocateResult(ItemStack itemStack, Album album) {
    }
}
