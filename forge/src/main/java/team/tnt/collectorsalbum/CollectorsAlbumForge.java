package team.tnt.collectorsalbum;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import team.tnt.collectorsalbum.client.CollectorsAlbumClient;
import team.tnt.collectorsalbum.common.init.*;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.common.resource.CardPackDropManager;
import team.tnt.collectorsalbum.platform.registration.ForgeRegistration;

@Mod(CollectorsAlbum.MOD_ID)
public class CollectorsAlbumForge {

    public CollectorsAlbumForge() {
        CollectorsAlbum.init();

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ForgeRegistration.subscribeRegistryEvent(eventBus, ItemRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, ItemGroupRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, CardTypeRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, CategoryRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, SoundRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, NumberProviderRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, ItemDropProviderRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, ItemDataComponentRegistry.REGISTRY);
        eventBus.addListener(this::createNewRegistries);
        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::addReloadListeners);
        forgeBus.addListener(this::playerTick);
        forgeBus.addListener(this::playerLoggedOut);
        forgeBus.addListener(this::serverStopping);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> CollectorsAlbumClient::construct);
    }

    private void setup(FMLCommonSetupEvent event) {
        CollectorsAlbum.NETWORK_MANAGER.bind();
    }

    private void clientSetup(FMLClientSetupEvent event) {
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
        event.addListener(CardPackDropManager.getInstance());
    }

    private void createNewRegistries(NewRegistryEvent event) {
        ForgeRegistration.bindCustomRegistries(event);
    }
}
