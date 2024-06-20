package team.tnt.collectorsalbum;

import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import team.tnt.collectorsalbum.common.init.ItemRegistry;
import team.tnt.collectorsalbum.common.registry.MultiLoaderRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;

@Mod(CollectorsAlbum.MOD_ID)
public class CollectorsAlbumNeoforge {

    public CollectorsAlbumNeoforge(IEventBus eventBus) {
        CollectorsAlbum.init();

        subscribeRegistry(eventBus, ItemRegistry.REGISTRY);

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

    private static <T> void subscribeRegistry(IEventBus eventBus, MultiLoaderRegistry<T> registry) {
        eventBus.addListener(EventPriority.NORMAL, false, RegisterEvent.class, event -> {
            ResourceKey<?> key = event.getRegistryKey();
            if (key.equals(registry.key())) {
                registry.register((identifier, provider) -> event.register(registry.key(), helper -> helper.register(identifier, provider.get())));
            }
        });
    }
}
