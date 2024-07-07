package team.tnt.collectorsalbum.integrations;

import net.minecraft.world.entity.player.Player;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumLocatorResult;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface PlayerAlbumLocatorRegistration {

    void register(BiConsumer<String, Supplier<AlbumFinder>> registration);

    @FunctionalInterface
    interface AlbumFinder {
        AlbumLocatorResult find(Player player, Album previousAlbum);
    }
}
