package team.tnt.collectorsalbum;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import team.tnt.collectorsalbum.common.init.ItemGroupRegistry;
import team.tnt.collectorsalbum.common.init.ItemRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.platform.resource.FabricReloadListenerWrapper;

public class CollectorsAlbumFabric implements ModInitializer {

    public CollectorsAlbumFabric() {
        CollectorsAlbum.init();
    }

    @Override
    public void onInitialize() {
        CollectorsAlbum.NETWORK_MANAGER.bind();
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> CollectorsAlbum.serverStopped());

        ResourceManagerHelper resourceManagerHelper = ResourceManagerHelper.get(PackType.SERVER_DATA);
        resourceManagerHelper.registerReloadListener(FabricReloadListenerWrapper.wrap(AlbumCategoryManager.getInstance()));
        resourceManagerHelper.registerReloadListener(FabricReloadListenerWrapper.wrap(AlbumCardManager.getInstance()));

        this.registerData();
    }

    private void registerData() {
        ItemRegistry.REGISTRY.bind();
        ItemGroupRegistry.REGISTRY.bind();
    }
}
