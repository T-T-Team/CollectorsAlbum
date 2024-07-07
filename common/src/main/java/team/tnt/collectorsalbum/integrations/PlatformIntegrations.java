package team.tnt.collectorsalbum.integrations;

import net.minecraft.world.entity.player.Player;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumLocatorResult;
import team.tnt.collectorsalbum.platform.JavaServiceLoader;
import team.tnt.collectorsalbum.platform.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class PlatformIntegrations {

    private static final List<ModAlbumFinder> ALBUM_FINDERS = new ArrayList<>();

    public static final PlayerAlbumLocatorRegistration LOCATOR_REGISTRATION = JavaServiceLoader.loadService(PlayerAlbumLocatorRegistration.class);

    public static void registerAlbumFinders() {
        LOCATOR_REGISTRATION.register((namespace, locator) -> ALBUM_FINDERS.add(new ModAlbumFinder(namespace, locator)));
    }

    public static AlbumLocatorResult getAlbumLocatorResult(Player player, Album previousAlbum) {
        for (ModAlbumFinder finder : ALBUM_FINDERS) {
            if (Platform.INSTANCE.isModLoaded(finder.mod())) {
                AlbumLocatorResult result = finder.locator().get().find(player, previousAlbum);
                if (result.exists()) {
                    return result;
                }
            }
        }
        return AlbumLocatorResult.notFound();
    }

    public record ModAlbumFinder(String mod, Supplier<PlayerAlbumLocatorRegistration.AlbumFinder> locator) {}
}
