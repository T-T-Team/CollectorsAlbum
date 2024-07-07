package team.tnt.collectorsalbum;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumLocatorResult;
import team.tnt.collectorsalbum.common.PlayerAlbumTracker;
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
        config = Configuration.registerConfig(CollectorsAlbumConfig.class, ConfigFormats.yaml()).getConfigInstance();
        registerPackets();
        PlatformIntegrations.registerAlbumFinders();
    }

    public static CollectorsAlbumConfig getConfig() {
        return config;
    }

    public static void tickPlayer(Player player) {
        PlayerAlbumTracker tracker = PlayerAlbumTracker.get();
        Album album = tracker.getAlbum(player).orElse(null);
        if (album == null) {
            AlbumLocatorResult result = tracker.findAlbum(player, null);
            if (!result.exists())
                return;
            album = result.getAlbum();
            tracker.cacheAlbum(player, album);
        } else if (player.level().getGameTime() % 50L == 0L) {
            AlbumLocatorResult result = tracker.findAlbum(player, album);
            if (!result.exists() || result.getAlbum().test(album)) {
                // TODO album removed
                // FIXME NPE when result does not exist and ticking continues
            }
        }
        album.tick(player);
    }

    public static void sendPlayerDatapacks(ServerPlayer player) {
        getDatapackSyncPayloads().forEach(payload -> PlatformNetworkManager.NETWORK.sendClientMessage(player, payload));
    }

    public static List<CustomPacketPayload> getDatapackSyncPayloads() {
        List<CustomPacketPayload> payloads = new ArrayList<>();
        payloads.add(new S2C_SendDatapackResources<>(AlbumCardManager.getInstance()));
        payloads.add(new S2C_SendDatapackResources<>(AlbumCategoryManager.getInstance()));
        // TODO bonuses
        return payloads;
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
