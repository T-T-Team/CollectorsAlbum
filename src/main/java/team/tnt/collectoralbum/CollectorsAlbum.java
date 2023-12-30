package team.tnt.collectoralbum;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.tnt.collectoralbum.client.CollectorsAlbumClient;
import team.tnt.collectoralbum.common.CreativeTabs;
import team.tnt.collectoralbum.common.init.ItemRegistry;
import team.tnt.collectoralbum.common.init.MenuTypes;
import team.tnt.collectoralbum.common.init.SoundRegistry;
import team.tnt.collectoralbum.config.ModConfig;
import team.tnt.collectoralbum.data.boosts.AlbumCardBoostManager;
import team.tnt.collectoralbum.data.collectibles.CardGroupManager;
import team.tnt.collectoralbum.data.collectibles.CollectibleCardManager;
import team.tnt.collectoralbum.data.packs.CardPackLootManager;
import team.tnt.collectoralbum.network.Networking;

@Mod(CollectorsAlbum.MODID)
public class CollectorsAlbum {

    public static final Logger LOGGER = LogManager.getLogger(CollectorsAlbum.class);
    public static final String MODID = "collectorsalbum";

    public static final CardPackLootManager CARD_PACK_MANAGER = new CardPackLootManager();
    public static final AlbumCardBoostManager ALBUM_CARD_BOOST_MANAGER = new AlbumCardBoostManager();
    public static final CardGroupManager GROUP_MANAGER = new CardGroupManager();
    public static final CollectibleCardManager CARD_MANAGER = new CollectibleCardManager();
    public static ModConfig config;

    public CollectorsAlbum() {
        config = Configuration.registerConfig(ModConfig.class, ConfigFormats.yaml()).getConfigInstance();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::loadCommon);
        eventBus.addListener(this::loadClient);

        ItemRegistry.REGISTRY.register(eventBus);
        SoundRegistry.REGISTRY.register(eventBus);
        MenuTypes.REGISTRY.register(eventBus);
        CreativeTabs.REGISTER.register(eventBus);

        MinecraftForge.EVENT_BUS.addListener(this::registerReloadListener);
    }

    private void loadClient(FMLClientSetupEvent event) {
        CollectorsAlbumClient client = CollectorsAlbumClient.getClient();
        MinecraftForge.EVENT_BUS.addListener(client::handleClientTick);
    }

    private void loadCommon(FMLCommonSetupEvent event) {
        Networking.registerPackets();
    }

    private void registerReloadListener(AddReloadListenerEvent event) {
        event.addListener(CARD_PACK_MANAGER);
        event.addListener(ALBUM_CARD_BOOST_MANAGER);
        event.addListener(GROUP_MANAGER);
        event.addListener(CARD_MANAGER);

        /*
        List<CardItem> cards = ForgeRegistries.ITEMS.getValues().stream().filter(card -> card instanceof CardItem)
                .map(t -> (CardItem) t).toList();
        Map<CardDefinition, List<CardItem>> cardGroups = cards.stream().collect(Collectors.groupingBy(CardItem::getCard));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File baseDir = new File("D:\\mcmods\\1.20-forge\\CollectorsAlbum\\src\\main\\resources\\data\\collectorsalbum\\collectibles");
        File cardDir = new File(baseDir, "cards");
        for (Map.Entry<CardDefinition, List<CardItem>> entry : cardGroups.entrySet()) {
            CardDefinition definition = entry.getKey();
            List<CardItem> list = entry.getValue().stream().sorted(Comparator.comparingInt(t -> t.getCardRarity().ordinal())).toList();

            ICardCategory category = definition.category();
            int number = CardCategoryIndexPool.getIndexOffset(category) + definition.cardNumber();
            String group = category.getId().toString();

            JsonObject object = new JsonObject();
            object.addProperty("cardNumber", number);
            object.addProperty("group", group);

            JsonObject itemMap = new JsonObject();
            for (CardItem item : list) {
                JsonObject props = new JsonObject();
                props.addProperty("rarity", item.getCardRarity().name().toUpperCase(Locale.ROOT));
                props.addProperty("extraPoints", 0);
                itemMap.add(ForgeRegistries.ITEMS.getKey(item).toString(), props);
            }
            object.add("itemMap", itemMap);

            File file = new File(cardDir, definition.cardId().getPath() + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                file.createNewFile();
                writer.write(gson.toJson(object));
            } catch (IOException e) {
                throw new RuntimeException("Failed to create card data", e);
            }
        }

        Map<ICardCategory, List<CardItem>> byCategory = cards.stream().collect(Collectors.groupingBy(item -> item.getCard().category()));
        File groupsDir = new File(baseDir, "groups");
        for (Map.Entry<ICardCategory, List<CardItem>> entry : byCategory.entrySet()) {
            ICardCategory category = entry.getKey();
            Set<String> items = entry.getValue().stream()
                    .map(t -> t.getCard().cardId().toString())
                    .collect(Collectors.toSet());

            JsonObject object = new JsonObject();
            JsonArray cardsJson = new JsonArray();
            items.forEach(cardsJson::add);
            object.add("cards", cardsJson);
            object.add("displayStyle", new JsonObject());

            File file = new File(groupsDir, category.getId().getPath() + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                file.createNewFile();
                writer.write(gson.toJson(object));
            } catch (IOException e) {
                throw new RuntimeException("Failed to create group data", e);
            }
        }
        */
    }
}