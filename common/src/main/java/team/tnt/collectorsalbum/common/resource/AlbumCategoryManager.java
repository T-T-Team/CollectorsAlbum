package team.tnt.collectorsalbum.common.resource;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.AlbumCategoryType;
import team.tnt.collectorsalbum.network.S2C_SendDatapackResources;
import team.tnt.collectorsalbum.platform.resource.PlatformGsonCodecReloadListener;

import java.util.*;

public final class AlbumCategoryManager extends PlatformGsonCodecReloadListener<AlbumCategory> {

    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "album_category_manager");
    private static final AlbumCategoryManager INSTANCE = new AlbumCategoryManager();
    private final Map<ResourceLocation, AlbumCategory> registeredCategories = new HashMap<>();

    private AlbumCategoryManager() {
        super("album/categories", AlbumCategoryType.INSTANCE_CODEC);
        S2C_SendDatapackResources.registerType(this);
    }

    public static AlbumCategoryManager getInstance() {
        return INSTANCE;
    }

    @Override
    public ResourceLocation identifier() {
        return IDENTIFIER;
    }

    public Optional<AlbumCategory> findById(ResourceLocation id) {
        return Optional.ofNullable(registeredCategories.get(id));
    }

    @Override
    protected void preApply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.registeredCategories.clear();
    }

    @Override
    protected void resolve(ResourceLocation path, AlbumCategory element) {
        if (this.registeredCategories.putIfAbsent(path, element) != null) {
            throw new IllegalArgumentException("Duplicate card category: " + element.identifier());
        }
    }

    @Override
    protected void onReloadComplete(ResourceManager manager, ProfilerFiller profiler) {
        super.onReloadComplete(manager, profiler);
    }

    @Override
    public List<AlbumCategory> getNetworkData() {
        return new ArrayList<>(registeredCategories.values());
    }

    @Override
    public void onNetworkDataReceived(List<AlbumCategory> collection) {
        registeredCategories.clear();
        collection.forEach(category -> registeredCategories.put(category.identifier(), category));
    }
}
