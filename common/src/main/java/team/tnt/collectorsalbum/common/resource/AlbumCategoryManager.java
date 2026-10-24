package team.tnt.collectorsalbum.common.resource;

import com.google.gson.JsonElement;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.platform.resource.PlatformGsonCodecReloadListener;
import team.tnt.collectorsalbum.util.PageInfo;

import java.util.*;

public final class AlbumCategoryManager extends PlatformGsonCodecReloadListener<AlbumCategory> implements SynchronizedResource<AlbumCategory> {

    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "album_category_manager");
    private static final AlbumCategoryManager INSTANCE = new AlbumCategoryManager();
    private final Map<Identifier, AlbumCategory> registeredCategories = new HashMap<>();
    private final Set<AlbumCategory> uniqueCategories = new LinkedHashSet<>();
    private List<PageInfo<AlbumCategory>> pagedCategories = Collections.emptyList();

    private AlbumCategoryManager() {
        super("album/categories", AlbumCategory.CODEC);
    }

    public static AlbumCategoryManager getInstance() {
        return INSTANCE;
    }

    public PageInfo<AlbumCategory> getPage(PageInfo<Identifier> pageInfo) {
        PageInfo<AlbumCategory> page = pageInfo.map(id -> this.findById(id).orElse(null));
        int index = this.getPageForCategory(page);
        if (index < 0) {
            return PageInfo.empty();
        }
        return page;
    }

    public PageInfo<AlbumCategory> getCategoryForPage(int page) {
        return page >= 0 && page < pagedCategories.size() ? pagedCategories.get(page) : PageInfo.empty();
    }

    public int getPageForCategory(AlbumCategory category) {
        return this.getPageForCategory(PageInfo.firstPage(category));
    }

    public int getPageForCategory(PageInfo<AlbumCategory> pageInfo) {
        return this.pagedCategories.indexOf(pageInfo);
    }

    public List<AlbumCategory> listBookmarkableCategories() {
        return this.listCategories().stream()
                .filter(category -> category.visualTemplate().hasBookmark())
                .toList();
    }

    /**
     * @return Count of cards which can be collected - used in Album statistics. Counts only unique card numbers per category (but this depends on implementation).
     */
    public int getCollectibleCardCount() {
        return this.uniqueCategories.stream().mapToInt(AlbumCategory::getCardCount).sum();
    }

    public Collection<AlbumCategory> listCategories() {
        return this.uniqueCategories;
    }

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }

    public Optional<AlbumCategory> findById(Identifier id) {
        return Optional.ofNullable(this.registeredCategories.get(id));
    }

    public Optional<AlbumCategory> find(PageInfo<Identifier> pageInfo) {
        return pageInfo.isEmpty() ? Optional.empty() : this.findById(pageInfo.element());
    }

    @Override
    protected void preApply(Map<Identifier, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.clearRegisteredData();
    }

    @Override
    protected void resolve(Identifier path, AlbumCategory element) {
        if (element.getCardCount() == 0) {
            return;
        }
        if (this.registeredCategories.putIfAbsent(element.identifier(), element) != null) {
            throw new IllegalArgumentException("Duplicate card category: " + element.identifier());
        }
    }

    @Override
    protected void onReloadComplete(ResourceManager manager, ProfilerFiller profiler) {
        super.onReloadComplete(manager, profiler);
        this.performRegistration(this.registeredCategories.values());
    }

    @Override
    public List<AlbumCategory> getDataForSync() {
        return new ArrayList<>(this.uniqueCategories);
    }

    @Override
    public synchronized void receiveNetworkData(List<AlbumCategory> collection) {
        this.clearRegisteredData();
        for (AlbumCategory category : collection) {
            this.registeredCategories.put(category.identifier(), category);
        }
        this.performRegistration(collection);
    }

    private List<AlbumCategory> getSortedUniqueCategories(Collection<AlbumCategory> collection) {
        return collection.stream()
                .sorted(Comparator.comparingInt(AlbumCategory::getPageOrder))
                .toList();
    }

    private List<PageInfo<AlbumCategory>> spreadIntoPages(Set<AlbumCategory> uniqueCategories) {
        List<PageInfo<AlbumCategory>> pages = new ArrayList<>();
        for (AlbumCategory category : uniqueCategories) {
            int pageCount = category.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PageInfo<AlbumCategory> pageInfo = new PageInfo<>(category, i);
                pages.add(pageInfo);
            }
        }
        return pages;
    }

    private void performRegistration(Collection<AlbumCategory> registry) {
        List<AlbumCategory> categories = this.getSortedUniqueCategories(registry);
        this.uniqueCategories.addAll(categories);
        this.pagedCategories = this.spreadIntoPages(this.uniqueCategories);
    }

    private void clearRegisteredData() {
        this.registeredCategories.clear();
        this.pagedCategories.clear();
        this.uniqueCategories.clear();
    }
}
