package team.tnt.collectorsalbum.integrations;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class FabricAlbumLocatorRegistration implements PlayerAlbumLocatorRegistration {

    @Override
    public void register(BiConsumer<String, Supplier<AlbumFinder>> registration) {
    }
}
