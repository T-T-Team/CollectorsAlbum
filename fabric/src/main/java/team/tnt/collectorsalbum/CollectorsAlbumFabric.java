package team.tnt.collectorsalbum;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import team.tnt.collectorsalbum.common.init.ItemRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.util.FabricJsonReloadListenerWrapper;

public class CollectorsAlbumFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CollectorsAlbum.init();

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> CollectorsAlbum.serverStopped());

        ResourceManagerHelper resourceManagerHelper = ResourceManagerHelper.get(PackType.SERVER_DATA);
        resourceManagerHelper.registerReloadListener(
                FabricJsonReloadListenerWrapper.wrap(ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "album_categories"), AlbumCategoryManager.getInstance())
        );
        resourceManagerHelper.registerReloadListener(
                FabricJsonReloadListenerWrapper.wrap(ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "album_cards"), AlbumCardManager.getInstance())
        );

        this.registerData();
    }

    private void registerData() {
        ItemRegistry.REGISTRY.register();
    }
}
