package team.tnt.collectorsalbum.common.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.AlbumCategory;

import java.util.HashMap;
import java.util.Map;

public final class AlbumCategoryManager extends MultiloaderJsonReloadListener {

    private static final AlbumCategoryManager INSTANCE = new AlbumCategoryManager();
    private final Map<ResourceLocation, AlbumCategory> registeredCategories = new HashMap<>();

    private AlbumCategoryManager() {
        super(new Gson(), "album/categories");
    }

    public static AlbumCategoryManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void loadData(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.registeredCategories.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation identifier = entry.getKey();
            JsonElement data = entry.getValue();
            try {
                // TODO parse
            } catch (Exception e) {
                CollectorsAlbum.LOGGER.error("Failed to load album category ID {} due to error {}", identifier, e);
            }
        }
    }
}
