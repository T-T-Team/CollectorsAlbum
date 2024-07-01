package team.tnt.collectorsalbum;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.init.*;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.platform.registration.CustomPlatformRegistryBindCallback;
import team.tnt.collectorsalbum.platform.registration.FabricRegistration;
import team.tnt.collectorsalbum.platform.resource.FabricReloadListenerWrapper;

public class CollectorsAlbumFabric implements ModInitializer {

    public CollectorsAlbumFabric() {
        CollectorsAlbum.init();
    }

    @Override
    public void onInitialize() {
        CollectorsAlbum.NETWORK_MANAGER.bind();
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> CollectorsAlbum.serverStopped());
        CustomPlatformRegistryBindCallback.EVENT.register(registry -> {
            if (registry.key().equals(CollectorsAlbumRegistries.Keys.CARD_TYPE_KEY)) {
                CardTypeRegistry.REGISTRY.bind();
            } else if (registry.key().equals(CollectorsAlbumRegistries.Keys.CARD_CATEGORY_KEY)) {
                CategoryRegistry.REGISTRY.bind();
            }
        });

        ResourceManagerHelper resourceManagerHelper = ResourceManagerHelper.get(PackType.SERVER_DATA);
        resourceManagerHelper.registerReloadListener(FabricReloadListenerWrapper.wrap(AlbumCategoryManager.getInstance()));
        resourceManagerHelper.registerReloadListener(FabricReloadListenerWrapper.wrap(AlbumCardManager.getInstance()));

        this.registerData();
    }

    private void registerData() {
        FabricRegistration.registerCustomRegistry(CollectorsAlbumRegistries.CARD_TYPE);
        FabricRegistration.registerCustomRegistry(CollectorsAlbumRegistries.CATEGORY);
        ItemRegistry.REGISTRY.bind();
        ItemGroupRegistry.REGISTRY.bind();
        SoundRegistry.REGISTRY.bind();
    }
}
