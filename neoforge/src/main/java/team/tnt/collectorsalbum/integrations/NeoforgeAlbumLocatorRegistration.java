package team.tnt.collectorsalbum.integrations;

import team.tnt.collectorsalbum.integrations.curios.CuriosIntegrationNeoforge;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class NeoforgeAlbumLocatorRegistration implements PlayerAlbumLocatorRegistration {

    @Override
    public void register(BiConsumer<String, Supplier<AlbumFinder>> registration) {
        registration.accept("curios", () -> CuriosIntegrationNeoforge::find);
    }
}
