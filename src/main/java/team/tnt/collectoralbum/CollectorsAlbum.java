package team.tnt.collectoralbum;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.tnt.collectoralbum.client.CollectorsAlbumClient;
import team.tnt.collectoralbum.common.init.ItemRegistry;
import team.tnt.collectoralbum.common.init.MenuTypes;
import team.tnt.collectoralbum.common.init.SoundRegistry;
import team.tnt.collectoralbum.config.ModConfig;
import team.tnt.collectoralbum.data.boosts.AlbumCardBoostManager;
import team.tnt.collectoralbum.data.packs.CardPackLootManager;
import team.tnt.collectoralbum.network.Networking;

@Mod(CollectorsAlbum.MODID)
public class CollectorsAlbum {

    public static final Logger LOGGER = LogManager.getLogger(CollectorsAlbum.class);
    public static final String MODID = "collectorsalbum";

    public static final CreativeModeTab TAB = new CreativeModeTab("collectorsalbum.tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegistry.ALBUM.get());
        }
    };

    public static final CardPackLootManager CARD_PACK_MANAGER = new CardPackLootManager();
    public static final AlbumCardBoostManager ALBUM_CARD_BOOST_MANAGER = new AlbumCardBoostManager();
    public static ModConfig config;

    public CollectorsAlbum() {
        config = Configuration.registerConfig(ModConfig.class, ConfigFormats.yaml()).getConfigInstance();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::loadCommon);

        ItemRegistry.REGISTRY.register(eventBus);
        SoundRegistry.REGISTRY.register(eventBus);
        MenuTypes.REGISTRY.register(eventBus);

        MinecraftForge.EVENT_BUS.addListener(this::registerReloadListener);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            CollectorsAlbumClient client = CollectorsAlbumClient.getClient();
            eventBus.addListener(client::synchInit);
            MinecraftForge.EVENT_BUS.addListener(client::handleClientTick);
        });
    }

    private void loadCommon(FMLCommonSetupEvent event) {
        Networking.registerPackets();
    }

    private void registerReloadListener(AddReloadListenerEvent event) {
        event.addListener(CARD_PACK_MANAGER);
        event.addListener(ALBUM_CARD_BOOST_MANAGER);
    }
}