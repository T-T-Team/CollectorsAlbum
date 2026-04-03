package team.tnt.collectorsalbum;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import team.tnt.collectorsalbum.client.CollectorsAlbumClient;
import team.tnt.collectorsalbum.common.command.CollectorsAlbumCommand;
import team.tnt.collectorsalbum.common.init.*;
import team.tnt.collectorsalbum.common.resource.*;
import team.tnt.collectorsalbum.integrations.PlatformIntegrations;
import team.tnt.collectorsalbum.integrations.curios.CuriosPlugin;
import team.tnt.collectorsalbum.platform.network.NeoforgeNetwork;
import team.tnt.collectorsalbum.platform.registration.NeoforgeRegistration;

import java.util.stream.Stream;

@Mod(CollectorsAlbum.MOD_ID)
public class CollectorsAlbumNeoforge {

    public CollectorsAlbumNeoforge(IEventBus eventBus) {
        PlatformIntegrations.registerStartupPlugin("curios", CuriosPlugin::instance);
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
        neoBus.addListener(this::onItemStartUse);
        neoBus.addListener(this::setPackUseDuration);
        neoBus.addListener(this::generateDrops);
        neoBus.addListener(this::addCommands);
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

    private void addReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(AlbumCardManager.IDENTIFIER, AlbumCardManager.getInstance());
        event.addListener(AlbumCategoryManager.IDENTIFIER, AlbumCategoryManager.getInstance());
        event.addListener(AlbumBonusManager.IDENTIFIER, AlbumBonusManager.getInstance());
        event.addListener(CardPackDropManager.IDENTIFIER, CardPackDropManager.getInstance());
        event.addListener(MobAdditionalDropManager.IDENTIFIER, MobAdditionalDropManager.getInstance());
        event.addDependency(AlbumCardManager.IDENTIFIER, AlbumCategoryManager.IDENTIFIER);
    }

    private void addRegistries(NewRegistryEvent event) {
        NeoforgeRegistration.bindNewRegistries(event);
    }

    private void addCommands(RegisterCommandsEvent event) {
        CollectorsAlbumCommand.register(event.getDispatcher());
    }

    private void onItemStartUse(PlayerInteractEvent.RightClickItem event) {
        if (event.isCanceled())
            return;
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.has(ItemDataComponentRegistry.PACK_DROPS_TABLE)) {
            player.startUsingItem(hand);
            player.playSound(SoundRegistry.PACK_OPEN.get(), 1.0F, 1.0F);
            event.setCancellationResult(InteractionResult.CONSUME);
        }
    }

    private void setPackUseDuration(LivingEntityUseItemEvent.Start event) {
        if (event.isCanceled())
            return;
        ItemStack itemStack = event.getItem();
        if (itemStack.has(ItemDataComponentRegistry.PACK_DROPS_TABLE)) {
            event.setDuration(20);
        }
    }

    private void generateDrops(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        ItemStack itemStack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack.has(ItemDataComponentRegistry.PACK_DROPS_TABLE) && entity instanceof ServerPlayer player) {
            CollectorsAlbum.openPack(player);
        }
    }
}
