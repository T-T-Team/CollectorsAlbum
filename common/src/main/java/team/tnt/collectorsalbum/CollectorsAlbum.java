package team.tnt.collectorsalbum;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.PlayerAlbumTracker;
import team.tnt.collectorsalbum.config.CollectorsAlbumConfig;
import team.tnt.collectorsalbum.network.NetworkManager;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;

public class CollectorsAlbum {

    public static final String MOD_ID = "collectorsalbum";
    public static final Logger LOGGER = LogManager.getLogger("CollectorsAlbum");
    public static final PlatformNetworkManager NETWORK_MANAGER = PlatformNetworkManager.create(CollectorsAlbum.MOD_ID);
    private static CollectorsAlbumConfig config;

    public static void init() {
        config = Configuration.registerConfig(CollectorsAlbumConfig.class, ConfigFormats.yaml()).getConfigInstance();
        registerPackets();
    }

    public static CollectorsAlbumConfig getConfig() {
        return config;
    }

    public static void tickPlayer(Player player) {
        PlayerAlbumTracker tracker = PlayerAlbumTracker.get();
        Album album = tracker.getAlbum(player).orElse(null);
        if (album == null) {
            PlayerAlbumTracker.AlbumLocateResult result = tracker.findAlbum(player).orElse(null);
            if (result == null)
                return;
            album = result.album();
            tracker.cacheAlbum(player, album);
        }
        album.tick(player);
    }

    public static void playerLoggedOut(Player player) {
        PlayerAlbumTracker tracker = PlayerAlbumTracker.get();
        tracker.deleteCachedAlbum(player.getUUID());
    }

    public static void serverStopped() {
        PlayerAlbumTracker tracker = PlayerAlbumTracker.get();
        tracker.clearCache();
    }

    private static void registerPackets() {
        NetworkManager.init();
    }
}
