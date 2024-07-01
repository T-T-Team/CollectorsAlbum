package team.tnt.collectorsalbum;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import team.tnt.collectorsalbum.common.init.CardTypeRegistry;
import team.tnt.collectorsalbum.common.init.CategoryRegistry;
import team.tnt.collectorsalbum.common.init.ItemGroupRegistry;
import team.tnt.collectorsalbum.common.init.ItemRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.platform.network.NeoforgeNetwork;
import team.tnt.collectorsalbum.platform.registration.NeoforgeRegistration;

@Mod(CollectorsAlbum.MOD_ID)
public class CollectorsAlbumNeoforge {

    public CollectorsAlbumNeoforge(IEventBus eventBus) {
        CollectorsAlbum.init();

        eventBus.addListener(this::addRegistries);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, ItemRegistry.REGISTRY);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, ItemGroupRegistry.REGISTRY);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, CardTypeRegistry.REGISTRY);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, CategoryRegistry.REGISTRY);
        NeoforgeNetwork.subscribeRegistryEvent(eventBus, CollectorsAlbum.NETWORK_MANAGER);

        IEventBus neoBus = NeoForge.EVENT_BUS;
        neoBus.addListener(this::addReloadListeners);
        neoBus.addListener(this::playerTick);
        neoBus.addListener(this::playerLoggedOut);
        neoBus.addListener(this::serverStopping);
    }

    private void playerTick(PlayerTickEvent.Post event) {
        CollectorsAlbum.tickPlayer(event.getEntity());
    }

    private void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        CollectorsAlbum.playerLoggedOut(event.getEntity());
    }

    private void serverStopping(ServerStoppingEvent event) {
        CollectorsAlbum.serverStopped();
    }

    private void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(AlbumCardManager.getInstance());
        event.addListener(AlbumCategoryManager.getInstance());
    }

    private void addRegistries(NewRegistryEvent event) {
        NeoforgeRegistration.bindNewRegistries(event);
    }
}
