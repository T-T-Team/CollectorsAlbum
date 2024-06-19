package team.tnt.collectorsalbum.common.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.AlbumCard;

import java.util.HashMap;
import java.util.Map;

public class AlbumCardManager extends MultiloaderJsonReloadListener {

    private static final AlbumCardManager INSTANCE = new AlbumCardManager();
    private final Map<ResourceLocation, AlbumCard> registeredCards = new HashMap<>();

    private AlbumCardManager() {
        super(new Gson(), "album/cards");
    }

    public static AlbumCardManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void loadData(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.registeredCards.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation identifier = entry.getKey();
            JsonElement data = entry.getValue();
            try {
                // TODO parse
            } catch (Exception e) {
                CollectorsAlbum.LOGGER.error("Failed to load album card ID {} due to error {}", identifier, e);
            }
        }
    }
}
