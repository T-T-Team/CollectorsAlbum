package team.tnt.collectorsalbum.integrations;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.tracking.PlayerAlbumTracker;
import team.tnt.collectorsalbum.platform.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class PlatformIntegrations {

    private static final Marker MARKER = MarkerManager.getMarker("Integrations");
    private static final List<StartupPlugin> STARTUP_PLUGINS = new ArrayList<>();

    public static void registerStartupPlugin(String mod, Supplier<StartupPlugin> plugin) {
        CollectorsAlbum.LOGGER.debug(MARKER, "Attempting to register startup plugin for '{}' mod", mod);
        if (Platform.INSTANCE.isModLoaded(mod)) {
            StartupPlugin startupPlugin = plugin.get();
            CollectorsAlbum.LOGGER.info(MARKER, "Mod '{}' is loaded, registering plugin {}", mod, startupPlugin);
            STARTUP_PLUGINS.add(startupPlugin);
        }
    }

    public static void onStartup() {
        STARTUP_PLUGINS.forEach(StartupPlugin::onStartup);
        // register as last
        PlayerAlbumTracker.get().registerFinder(PlayerAlbumTracker.VANILLA);
    }
}
