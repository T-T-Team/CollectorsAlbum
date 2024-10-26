package team.tnt.collectorsalbum.integrations;

import team.tnt.collectorsalbum.integrations.curios.CuriosIntegrationForge;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ForgeAlbumLocatorRegistration implements PlayerAlbumLocatorRegistration {

    @Override
    public void register(BiConsumer<String, Supplier<AlbumFinder>> registration) {
        registration.accept("curios", () -> CuriosIntegrationForge::find);
    }
}
