package team.tnt.collectorsalbum;

import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import team.tnt.collectorsalbum.common.init.ItemRegistry;
import team.tnt.collectorsalbum.common.registry.MultiLoaderRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;

@Mod(CollectorsAlbum.MOD_ID)
public class CollectorsAlbumForge {

    public CollectorsAlbumForge() {
        CollectorsAlbum.init();

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        subscribeRegistry(eventBus, ItemRegistry.REGISTRY);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::addReloadListeners);
        forgeBus.addListener(this::playerTick);
        forgeBus.addListener(this::playerLoggedOut);
        forgeBus.addListener(this::serverStopping);
    }

    private void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;
        CollectorsAlbum.tickPlayer(event.player);
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
                registry.register((identifier, provider) -> event.register(registry.key(), helper -> helper.register(identifier.getPath(), provider.get())));
            }
        });
    }
}
