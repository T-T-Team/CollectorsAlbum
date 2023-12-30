package team.tnt.collectoralbum.data.collectibles;

import api.tnt.collectoralbum.CollectorsAlbumApi;
import api.tnt.collectoralbum.cards.CardGroup;
import api.tnt.collectoralbum.cards.CardProperties;
import api.tnt.collectoralbum.cards.CollectibleCard;
import api.tnt.collectoralbum.data.CardGroupRegistry;
import api.tnt.collectoralbum.data.CollectibleCardRegistry;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.FormattedMessage;
import team.tnt.collectoralbum.common.PopulatedCardGroup;
import team.tnt.collectoralbum.common.PreparedCardGroup;
import team.tnt.collectoralbum.common.item.Card;
import team.tnt.collectoralbum.common.item.CardRarity;
import team.tnt.collectoralbum.common.item.NumberedCard;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class CollectibleCardManager extends SimpleJsonResourceReloadListener {

    private static final Marker MARKER = MarkerManager.getMarker("CardManager");
    private static final Gson GSON = new Gson();

    public CollectibleCardManager() {
        super(GSON, "collectibles/cards");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        CollectorsAlbumApi.LOGGER.debug(MARKER, "Loading custom cards");
        int skippedCount = 0;
        CollectibleCardRegistry registry = (CollectibleCardRegistry) CollectorsAlbumApi.getCardsRegistry();
        registry.clearRegistry();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation key = entry.getKey();
            try {
                CollectibleCard card = parseCardJson(key, entry.getValue().getAsJsonObject());
                card.getItems().forEach(item -> registry.registerEntry(item, card));
            } catch (JsonParseException e) {
                ++skippedCount;
                CollectorsAlbumApi.LOGGER.error("Unable to parse JSON for card '{}' - {}", key, e);
            } catch (Exception e) {
                ++skippedCount;
                CollectorsAlbumApi.LOGGER.fatal(new FormattedMessage("Fatal error while parsing JSON data for card {}", key), e);
            }
        }
        CardGroupRegistry groupRegistry = (CardGroupRegistry) CollectorsAlbumApi.getGroupsRegistry();
        populateGroups(groupRegistry, registry);
        CollectorsAlbumApi.LOGGER.info(MARKER, "Card loading has finished. Loaded {} cards and skipped {} invalid definitions", registry.getRegistrySize(), skippedCount);
    }

    private void populateGroups(CardGroupRegistry registry, CollectibleCardRegistry cardRegistry) {
        CollectorsAlbumApi.LOGGER.debug(MARKER, "Populating card groups with loaded cards");
        Map<ResourceLocation, CardGroup> map = registry.getPreparedGroups();
        map.forEach((key, value) -> {
            try {
                PreparedCardGroup preparedCardGroup = (PreparedCardGroup) value;
                PopulatedCardGroup populatedCardGroup = preparedCardGroup.populate(cardRegistry);
                registry.registerEntry(key, populatedCardGroup);
            } catch (IllegalStateException e) {
                CollectorsAlbumApi.LOGGER.error(MARKER, "Error populating \"{}\" group due to error: {}", key, e);
            }
        });
        registry.setLoadingState(false);
        registry.clearLoadedData();
        CollectorsAlbumApi.LOGGER.info(MARKER, "Card group population has been completed");
    }

    private CollectibleCard parseCardJson(ResourceLocation identifier, JsonObject data) throws JsonParseException {
        boolean isIndexedCard = data.has("cardNumber");
        ResourceLocation groupKey = new ResourceLocation(GsonHelper.getAsString(data, "group"));
        CardGroup group = CollectorsAlbumApi.getGroupsRegistry().getValue(groupKey)
                .orElseThrow(() -> new JsonSyntaxException("Unknown group: " + groupKey));
        Map<Item, CardProperties> propertiesMap = new HashMap<>();
        JsonObject itemsJson = GsonHelper.getAsJsonObject(data, "itemMap");
        for (Map.Entry<String, JsonElement> entry : itemsJson.entrySet()) {
            ResourceLocation itemId = new ResourceLocation(entry.getKey());
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item == Items.AIR) {
                throw new JsonSyntaxException("Unknown item: " + itemId);
            }
            JsonObject propertiesJson = entry.getValue().getAsJsonObject();
            String rarityName = GsonHelper.getAsString(propertiesJson, "rarity");
            int extraPoints = GsonHelper.getAsInt(propertiesJson, "extraPoints", 0);
            CardRarity rarity;
            try {
                rarity = CardRarity.valueOf(rarityName.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new JsonSyntaxException("Unknown rarity: " + rarityName);
            }
            CardProperties properties = new CardProperties(rarity, extraPoints);
            propertiesMap.put(item, properties);
        }
        int index = GsonHelper.getAsInt(data, "cardNumber", 0);
        return isIndexedCard ? new NumberedCard(identifier, group, propertiesMap, index) : new Card(identifier, group, propertiesMap);
    }
}
