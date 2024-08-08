package team.tnt.collectorsalbum;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumLocatorResult;
import team.tnt.collectorsalbum.common.PlayerAlbumTracker;
import team.tnt.collectorsalbum.common.resource.AlbumBonusManager;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.config.CollectorsAlbumConfig;
import team.tnt.collectorsalbum.integrations.PlatformIntegrations;
import team.tnt.collectorsalbum.network.NetworkManager;
import team.tnt.collectorsalbum.network.S2C_SendDatapackResources;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;

import java.util.ArrayList;
import java.util.List;

public class CollectorsAlbum {

    public static final String MOD_ID = "collectorsalbum";
    public static final Logger LOGGER = LogManager.getLogger("CollectorsAlbum");
    public static final PlatformNetworkManager NETWORK_MANAGER = PlatformNetworkManager.create(CollectorsAlbum.MOD_ID);
    private static CollectorsAlbumConfig config;

    public static void init() {
        config = Configuration.registerConfig(CollectorsAlbumConfig.class, ConfigFormats.YAML).getConfigInstance();
        registerPackets();
        PlatformIntegrations.registerAlbumFinders();
    }

    public static CollectorsAlbumConfig getConfig() {
        return config;
    }

    public static void tickPlayer(Player player) {
        Level level = player.level();
        long time = level.getGameTime();
        if (level.isClientSide() || time % 100L != 0L) {
            return;
        }
        actuallyTickPlayer(player);
    }

    public static void forceAlbumReload(Player player) {
        if (!player.level().isClientSide())
            actuallyTickPlayer(player);
    }

    private static void actuallyTickPlayer(Player player) {
        PlayerAlbumTracker tracker = PlayerAlbumTracker.get();
        Album album = tracker.getAlbum(player).orElse(null);
        AlbumLocatorResult result;
        if (album == null) {
            result = tracker.findAlbum(player, null);
            if (!result.exists()) {
                return;
            }
            album = result.getAlbum();
            tracker.cacheAlbum(player, album);
        } else {
            result = tracker.findAlbum(player, album);
            if (!result.exists() || !result.getAlbum().test(album)) {
                tracker.deleteCachedAlbum(player.getUUID());
                album.removed(player);
                if (result.getAlbum() != null) {
                    tracker.cacheAlbum(player, result.getAlbum());
                    result.getAlbum().tick(player);
                }
                return;
            }
        }
        album.tick(player);
    }

    public static void sendPlayerDatapacks(ServerPlayer player) {
        PlatformNetworkManager.NETWORK.sendClientMessage(player, new S2C_SendDatapackResources());
        forceAlbumReload(player);
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
