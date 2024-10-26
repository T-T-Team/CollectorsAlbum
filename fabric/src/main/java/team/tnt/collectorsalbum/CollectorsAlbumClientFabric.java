package team.tnt.collectorsalbum;

import net.fabricmc.api.ClientModInitializer;
import team.tnt.collectorsalbum.client.CollectorsAlbumClient;
import team.tnt.collectorsalbum.platform.Side;
import team.tnt.collectorsalbum.platform.resource.MenuScreenRegistration;

public class CollectorsAlbumClientFabric implements ClientModInitializer {

    public CollectorsAlbumClientFabric() {
        CollectorsAlbumClient.construct();
    }

    @Override
    public void onInitializeClient() {
        CollectorsAlbumClient.init();
        MenuScreenRegistration.bind();
        CollectorsAlbum.NETWORK_MANAGER.bind(Side.CLIENT);
    }
}
