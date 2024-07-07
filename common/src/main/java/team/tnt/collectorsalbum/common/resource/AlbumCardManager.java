package team.tnt.collectorsalbum.common.resource;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.AlbumCard;
import team.tnt.collectorsalbum.common.AlbumCardType;
import team.tnt.collectorsalbum.network.S2C_SendDatapackResources;
import team.tnt.collectorsalbum.platform.resource.PlatformGsonCodecReloadListener;

import java.util.*;

public class AlbumCardManager extends PlatformGsonCodecReloadListener<AlbumCard> {

    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "album_card_manager");
    private static final AlbumCardManager INSTANCE = new AlbumCardManager();
    public static final Codec<AlbumCard> BY_NAME_CODEC = ResourceLocation.CODEC.comapFlatMap(
            identifier -> {
                AlbumCard card = INSTANCE.registeredCards.get(identifier);
                return card != null ? DataResult.success(card) : DataResult.error(() -> "Unknown card: " + identifier);
            }, AlbumCard::identifier
    );
    private final Map<ResourceLocation, AlbumCard> registeredCards = new HashMap<>();
    private final Map<Item, AlbumCard> byItemMap = new HashMap<>();

    private AlbumCardManager() {
        super("album/cards", AlbumCardType.INSTANCE_CODEC);
        S2C_SendDatapackResources.registerType(this);
    }

    public static AlbumCardManager getInstance() {
        return INSTANCE;
    }

    public Optional<AlbumCard> getCardInfo(Item item) {
        return Optional.ofNullable(this.byItemMap.get(item));
    }

    public AlbumCard getCardById(ResourceLocation location) {
        return this.registeredCards.get(location);
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

    @Override
    public List<AlbumCard> getNetworkData() {
        return new ArrayList<>(this.registeredCards.values());
    }

    @Override
    public void onNetworkDataReceived(List<AlbumCard> collection) {
        registeredCards.clear();
        byItemMap.clear();
        collection.forEach(card -> {
            registeredCards.put(card.identifier(), card);
            byItemMap.put(card.asItem().getItem(), card);
        });
    }
}
