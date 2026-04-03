package team.tnt.collectorsalbum.common.resource;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.card.AlbumCardType;
import team.tnt.collectorsalbum.platform.resource.PlatformGsonCodecReloadListener;

import java.util.*;
import java.util.stream.Collectors;

public class AlbumCardManager extends PlatformGsonCodecReloadListener<AlbumCard> implements SynchronizedResource<AlbumCard> {

    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "album_card_manager");
    private static final AlbumCardManager INSTANCE = new AlbumCardManager();
    public static final Codec<AlbumCard> BY_NAME_CODEC = Identifier.CODEC.comapFlatMap(
            identifier -> {
                AlbumCard card = INSTANCE.registeredCards.get(identifier);
                return card != null ? DataResult.success(card) : DataResult.error(() -> "Unknown card: " + identifier);
            }, AlbumCard::identifier
    );
    private final Map<Identifier, AlbumCard> registeredCards = new HashMap<>();
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

    public AlbumCard getCardById(Identifier location) {
        return this.registeredCards.get(location);
    }

    public boolean isCard(Item item) {
        return byItemMap.containsKey(item);
    }

    public boolean isCard(ItemStack itemStack) {
        return this.isCard(itemStack.getItem());
    }

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }

    public List<ItemStack> processDrops(List<ItemStack> drops) {
        return drops.stream().filter(this::isCard)
                .map(ItemStack::copy)
                .collect(Collectors.toList());
    }

    public Map<Item, AlbumCard> getByItemMap() {
        return Collections.unmodifiableMap(this.byItemMap);
    }

    @Override
    protected void preApply(Map<Identifier, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.registeredCards.clear();
        this.byItemMap.clear();
    }

    @Override
    protected void resolve(Identifier path, AlbumCard element) {
        if (!element.enabled()) {
            return;
        }
        if (this.registeredCards.putIfAbsent(element.identifier(), element) != null) {
            throw new IllegalArgumentException("Duplicate card with ID: " + element.identifier());
        }
        Item item = element.asItem();
        if (this.byItemMap.putIfAbsent(item, element) != null) {
            throw new IllegalArgumentException(String.format("Duplicate item registered as a card in %s and %s cards", path.toString(), byItemMap.get(item).identifier()));
        }
    }

    @Override
    public List<AlbumCard> getDataForSync() {
        return new ArrayList<>(this.registeredCards.values());
    }

    @Override
    public synchronized void receiveNetworkData(List<AlbumCard> collection) {
        registeredCards.clear();
        byItemMap.clear();
        collection.forEach(card -> {
            registeredCards.put(card.identifier(), card);
            byItemMap.put(card.asItem(), card);
        });
    }
}
