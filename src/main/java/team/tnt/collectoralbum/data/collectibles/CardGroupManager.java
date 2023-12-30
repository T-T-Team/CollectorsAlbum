package team.tnt.collectoralbum.data.collectibles;

import api.tnt.collectoralbum.CollectorsAlbumApi;
import api.tnt.collectoralbum.cards.CardGroup;
import api.tnt.collectoralbum.data.CardGroupRegistry;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.FormattedMessage;
import team.tnt.collectoralbum.common.PreparedCardGroup;

import java.util.Map;

public class CardGroupManager extends SimpleJsonResourceReloadListener {

    private static final Marker MARKER = MarkerManager.getMarker("CardGroups");
    private static final Gson GSON = new Gson();

    public CardGroupManager() {
        super(GSON, "collectibles/groups");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        CollectorsAlbumApi.LOGGER.debug(MARKER, "Loading card groups");
        CardGroupRegistry registry = (CardGroupRegistry) CollectorsAlbumApi.getGroupsRegistry();
        registry.clearRegistry();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation key = entry.getKey();
            try {
                DataResult<PreparedCardGroup> result = PreparedCardGroup.CODEC_PROVIDER.apply(key).parse(JsonOps.INSTANCE, entry.getValue());
                CardGroup group = result.getOrThrow(false, err -> CollectorsAlbumApi.LOGGER.error("Unable to parse card group data: {}", err));
                registry.savePreloadedGroup(key, group);
            } catch (Exception e) {
                CollectorsAlbumApi.LOGGER.error(new FormattedMessage("Fatal error while parsing JSON data for card group {}", key), e);
            }
        }
        CollectorsAlbumApi.LOGGER.info(MARKER, "Card group pre-loading has finished");
    }
}
