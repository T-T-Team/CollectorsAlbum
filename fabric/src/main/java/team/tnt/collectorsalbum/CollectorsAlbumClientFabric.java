package team.tnt.collectorsalbum;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import team.tnt.collectorsalbum.client.CollectorsAlbumClient;
import team.tnt.collectorsalbum.client.screen.pip.ScalableItemPip;
import team.tnt.collectorsalbum.platform.resource.MenuScreenRegistration;

public class CollectorsAlbumClientFabric implements ClientModInitializer {

    public CollectorsAlbumClientFabric() {
        CollectorsAlbumClient.construct();
        ClientTickEvents.END_CLIENT_TICK.register(client -> CollectorsAlbumClient.clientTick());
    }

    @Override
    public void onInitializeClient() {
        CollectorsAlbumClient.init();
        MenuScreenRegistration.bind();
        PictureInPictureRendererRegistry.register(_ -> new ScalableItemPip());
    }
}
