package team.tnt.collectorsalbum;

import net.fabricmc.api.ClientModInitializer;
import team.tnt.collectorsalbum.client.CollectorsAlbumClient;

public class CollectorsAlbumClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CollectorsAlbumClient.construct();
    }
}
