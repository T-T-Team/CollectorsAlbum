package team.tnt.collectorsalbum;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import team.tnt.collectorsalbum.client.CollectorsAlbumClient;
import team.tnt.collectorsalbum.common.command.CollectorsAlbumCommand;
import team.tnt.collectorsalbum.common.init.*;
import team.tnt.collectorsalbum.common.resource.*;
import team.tnt.collectorsalbum.platform.registration.ForgeRegistration;
import team.tnt.collectorsalbum.platform.resource.MenuScreenRegistration;

@Mod(CollectorsAlbum.MOD_ID)
public class CollectorsAlbumForge {

    public CollectorsAlbumForge(FMLJavaModLoadingContext ctx) {
        CollectorsAlbum.init();

        IEventBus eventBus = ctx.getModEventBus();
        ForgeRegistration.subscribeRegistryEvent(eventBus, BlockRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, ItemRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, ItemGroupRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, CardTypeRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, CategoryRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, SoundRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, NumberProviderRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, ItemDropProviderRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, ItemDataComponentRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, AlbumBonusRegistry.REGISTRY);
        ForgeRegistration.subscribeRegistryEvent(eventBus, MenuRegistry.REGISTRY);
        eventBus.addListener(this::createNewRegistries);
        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::addReloadListeners);
        forgeBus.addListener(this::playerTick);
        forgeBus.addListener(this::playerLoggedOut);
        forgeBus.addListener(this::serverStopping);
        forgeBus.addListener(this::onItemStartUse);
        forgeBus.addListener(this::setPackUseDuration);
        forgeBus.addListener(this::generateDrops);
        forgeBus.addListener(this::addCommands);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> CollectorsAlbumClient::construct);
    }

    private void setup(FMLCommonSetupEvent event) {
        CollectorsAlbum.NETWORK_MANAGER.bind();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        CollectorsAlbumClient.init();
        event.enqueueWork(() -> MenuScreenRegistration.bind());
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
        event.addListener(AlbumBonusManager.getInstance());
        event.addListener(CardPackDropManager.getInstance());
        event.addListener(MobAdditionalDropManager.getInstance());
    }

    private void createNewRegistries(NewRegistryEvent event) {
        ForgeRegistration.bindCustomRegistries(event);
    }

    private void onItemStartUse(PlayerInteractEvent.RightClickItem event) {
        if (event.isCanceled())
            return;
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.has(ItemDataComponentRegistry.PACK_DROPS_TABLE.get())) {
            player.startUsingItem(hand);
            player.playSound(SoundRegistry.PACK_OPEN.get(), 1.0F, 1.0F);
            event.setCancellationResult(InteractionResult.CONSUME);
        }
    }

    private void setPackUseDuration(LivingEntityUseItemEvent.Start event) {
        if (event.isCanceled())
            return;
        ItemStack itemStack = event.getItem();
        if (itemStack.has(ItemDataComponentRegistry.PACK_DROPS_TABLE.get())) {
            event.setDuration(20);
        }
    }

    private void generateDrops(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        ItemStack itemStack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack.has(ItemDataComponentRegistry.PACK_DROPS_TABLE.get()) && entity instanceof ServerPlayer player) {
            CollectorsAlbum.openPack(player);
        }
    }

    private void addCommands(RegisterCommandsEvent event) {
        CollectorsAlbumCommand.register(event.getDispatcher());
    }
}
