package team.tnt.collectorsalbum.integrations;

import team.tnt.collectorsalbum.integrations.trinkets.TrinketsIntegrationFabric;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class FabricAlbumLocatorRegistration implements PlayerAlbumLocatorRegistration {

    @Override
    public void register(BiConsumer<String, Supplier<AlbumFinder>> registration) {
        registration.accept("trinkets", () -> TrinketsIntegrationFabric::findTrinketAlbum);
    }
}
