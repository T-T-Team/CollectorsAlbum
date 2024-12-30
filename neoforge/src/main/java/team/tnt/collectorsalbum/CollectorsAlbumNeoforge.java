package team.tnt.collectorsalbum;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import team.tnt.collectorsalbum.client.CollectorsAlbumClient;
import team.tnt.collectorsalbum.common.init.*;
import team.tnt.collectorsalbum.common.resource.*;
import team.tnt.collectorsalbum.platform.network.NeoforgeNetwork;
import team.tnt.collectorsalbum.platform.registration.NeoforgeRegistration;
import team.tnt.collectorsalbum.platform.resource.MenuScreenRegistration;

import java.util.stream.Stream;

@Mod(CollectorsAlbum.MOD_ID)
public class CollectorsAlbumNeoforge {

    public CollectorsAlbumNeoforge(IEventBus eventBus) {
        CollectorsAlbum.init();

        eventBus.addListener(this::addRegistries);
        eventBus.addListener(this::clientSetup);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, BlockRegistry.REGISTRY);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, ItemRegistry.REGISTRY);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, ItemGroupRegistry.REGISTRY);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, CardTypeRegistry.REGISTRY);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, CategoryRegistry.REGISTRY);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, SoundRegistry.REGISTRY);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, NumberProviderRegistry.REGISTRY);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, ItemDropProviderRegistry.REGISTRY);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, ItemDataComponentRegistry.REGISTRY);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, AlbumBonusRegistry.REGISTRY);
        NeoforgeRegistration.subscribeRegistryEvent(eventBus, MenuRegistry.REGISTRY);
        NeoforgeNetwork.subscribeRegistryEvent(eventBus, CollectorsAlbum.NETWORK_MANAGER);

        IEventBus neoBus = NeoForge.EVENT_BUS;
        neoBus.addListener(this::addReloadListeners);
        neoBus.addListener(this::playerTick);
        neoBus.addListener(this::onDatapackSync);
        neoBus.addListener(this::playerLoggedOut);
        neoBus.addListener(this::serverStopping);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            CollectorsAlbumClient.construct();
            eventBus.addListener(this::registerScreens);
        }
    }

    private void clientSetup(FMLClientSetupEvent event) {
        CollectorsAlbumClient.init();
    }

    private void playerTick(PlayerTickEvent.Post event) {
        CollectorsAlbum.tickPlayer(event.getEntity());
    }

    private void onDatapackSync(OnDatapackSyncEvent event) {
        Stream<ServerPlayer> affectedPlayers = event.getRelevantPlayers();
        affectedPlayers.forEach(CollectorsAlbum::sendPlayerDatapacks);
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
        event.addListener(AlbumBonusManager.getInstance());
        event.addListener(CardPackDropManager.getInstance());
        event.addListener(MobAdditionalDropManager.getInstance());
    }

    private void addRegistries(NewRegistryEvent event) {
        NeoforgeRegistration.bindNewRegistries(event);
    }

    @OnlyIn(Dist.CLIENT)
    private void registerScreens(RegisterMenuScreensEvent event) {
        MenuScreenRegistration.bindRefs(event::register);
    }
}
