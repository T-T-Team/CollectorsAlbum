package team.tnt.collectorsalbum;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.init.*;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.common.resource.CardPackDropManager;
import team.tnt.collectorsalbum.platform.registration.CustomPlatformRegistryBindCallback;
import team.tnt.collectorsalbum.platform.registration.FabricRegistration;
import team.tnt.collectorsalbum.platform.resource.FabricReloadListenerWrapper;

public class CollectorsAlbumFabric implements ModInitializer {

    public CollectorsAlbumFabric() {
        CollectorsAlbum.init();
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> CollectorsAlbum.serverStopped());
        CustomPlatformRegistryBindCallback.EVENT.register(registry -> {
            if (registry.key().equals(CollectorsAlbumRegistries.Keys.CARD_TYPE_KEY)) {
                CardTypeRegistry.REGISTRY.bind();
            } else if (registry.key().equals(CollectorsAlbumRegistries.Keys.CARD_CATEGORY_KEY)) {
                CategoryRegistry.REGISTRY.bind();
            } else if (registry.key().equals(CollectorsAlbumRegistries.Keys.NUMBER_PROVIDER)) {
                NumberProviderRegistry.REGISTRY.bind();
            } else if (registry.key().equals(CollectorsAlbumRegistries.Keys.ITEM_DROP_PROVIDER)) {
                ItemDropProviderRegistry.REGISTRY.bind();
            }
        });

        this.registerData();

        ResourceManagerHelper resourceManagerHelper = ResourceManagerHelper.get(PackType.SERVER_DATA);
        resourceManagerHelper.registerReloadListener(FabricReloadListenerWrapper.of(AlbumCategoryManager.getInstance()));
        resourceManagerHelper.registerReloadListener(FabricReloadListenerWrapper.of(AlbumCardManager.getInstance()));
        resourceManagerHelper.registerReloadListener(FabricReloadListenerWrapper.of(CardPackDropManager.getInstance()));

        CollectorsAlbum.NETWORK_MANAGER.bind();
    }

    private void registerData() {
        FabricRegistration.registerCustomRegistry(CollectorsAlbumRegistries.CARD_TYPE);
        FabricRegistration.registerCustomRegistry(CollectorsAlbumRegistries.CATEGORY);
        FabricRegistration.registerCustomRegistry(CollectorsAlbumRegistries.NUMBER_PROVIDER);
        FabricRegistration.registerCustomRegistry(CollectorsAlbumRegistries.ITEM_DROP_PROVIDER);
        ItemRegistry.REGISTRY.bind();
        ItemGroupRegistry.REGISTRY.bind();
        SoundRegistry.REGISTRY.bind();
        ItemDataComponentRegistry.REGISTRY.bind();
    }
}
