package team.tnt.collectorsalbum.common.resource;

import com.google.gson.JsonElement;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.AlbumCategoryType;
import team.tnt.collectorsalbum.platform.resource.PlatformGsonCodecReloadListener;

import java.util.*;

public final class AlbumCategoryManager extends PlatformGsonCodecReloadListener<AlbumCategory> implements SynchronizedResource<AlbumCategory> {

    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "album_category_manager");
    private static final AlbumCategoryManager INSTANCE = new AlbumCategoryManager();
    private final Map<Identifier, AlbumCategory> registeredCategories = new HashMap<>();
    private final List<AlbumCategory> pagedCategories = new ArrayList<>();

    private AlbumCategoryManager() {
        super("album/categories", AlbumCategoryType.INSTANCE_CODEC);
    }

    public static AlbumCategoryManager getInstance() {
        return INSTANCE;
    }

    public AlbumCategory getCategoryForPage(int page) {
        return page >= 0 && page < pagedCategories.size() ? pagedCategories.get(page) : null;
    }

    public int getPageForCategory(AlbumCategory category) {
        return pagedCategories.indexOf(category);
    }

    public List<AlbumCategory> listBookmarkableCategories() {
        return pagedCategories.stream().filter(cat -> cat.visualTemplate().bookmarkIcon.isPresent()).toList();
    }

    /**
     * @return Count of cards which can be collected - used in Album statistics. Counts only unique card numbers per category (but this depends on implementation).
     */
    public int getCollectibleCardCount() {
        return this.pagedCategories.stream().mapToInt(category -> category.getCardNumbers().length).sum();
    }

    public Collection<AlbumCategory> listCategories() {
        return this.registeredCategories.values();
    }

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }

    public Optional<AlbumCategory> findById(Identifier id) {
        return Optional.ofNullable(registeredCategories.get(id));
    }

    @Override
    protected void preApply(Map<Identifier, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.registeredCategories.clear();
        this.pagedCategories.clear();
    }

    @Override
    protected void resolve(Identifier path, AlbumCategory element) {
        if (element.getCardNumbers().length == 0) {
            return;
        }
        if (this.registeredCategories.putIfAbsent(element.identifier(), element) != null) {
            throw new IllegalArgumentException("Duplicate card category: " + element.identifier());
        }
    }

    @Override
    protected void onReloadComplete(ResourceManager manager, ProfilerFiller profiler) {
        super.onReloadComplete(manager, profiler);
        this.registeredCategories.values().stream().sorted(Comparator.comparingInt(AlbumCategory::getPageOrder))
                .forEach(pagedCategories::add);
    }

    @Override
    public List<AlbumCategory> getDataForSync() {
        return new ArrayList<>(registeredCategories.values());
    }

    @Override
    public synchronized void receiveNetworkData(List<AlbumCategory> collection) {
        registeredCategories.clear();
        pagedCategories.clear();
        collection.forEach(category -> registeredCategories.put(category.identifier(), category));
        pagedCategories.addAll(collection.stream().sorted(Comparator.comparingInt(AlbumCategory::getPageOrder)).toList());
    }
}
