package team.tnt.collectorsalbum.common.resource;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.AlbumCard;
import team.tnt.collectorsalbum.common.AlbumCardType;
import team.tnt.collectorsalbum.platform.resource.PlatformGsonCodecReloadListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AlbumCardManager extends PlatformGsonCodecReloadListener<AlbumCard> {

    private static final AlbumCardManager INSTANCE = new AlbumCardManager();
    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "album_card_manager");
    private final Map<ResourceLocation, AlbumCard> registeredCards = new HashMap<>();
    private final Map<Item, AlbumCard> byItemMap = new HashMap<>();

    private AlbumCardManager() {
        super("album/cards", AlbumCardType.INSTANCE_CODEC);
    }

    public static AlbumCardManager getInstance() {
        return INSTANCE;
    }

    public Optional<AlbumCard> getCardInfo(Item item) {
        return Optional.ofNullable(this.byItemMap.get(item));
    }

    public boolean isCard(Item item) {
        return byItemMap.containsKey(item);
    }

    @Override
    public ResourceLocation identifier() {
        return IDENTIFIER;
    }

    @Override
    protected void preApply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.registeredCards.clear();
        this.byItemMap.clear();
    }

    @Override
    protected void resolve(ResourceLocation path, AlbumCard element) {
        if (this.registeredCards.putIfAbsent(element.identifier(), element) != null) {
            throw new IllegalArgumentException("Duplicate card with ID: " + element.identifier());
        }
        ItemStack itemStack = element.asItem();
        Item item = itemStack.getItem();
        if (this.byItemMap.putIfAbsent(item, element) != null) {
            throw new IllegalArgumentException(String.format("Duplicate item registered as a card in %s and %s cards", path.toString(), byItemMap.get(item).identifier()));
        }
    }
}
