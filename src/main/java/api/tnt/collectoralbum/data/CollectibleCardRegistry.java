package api.tnt.collectoralbum.data;

import api.tnt.collectoralbum.CollectorsAlbumApi;
import api.tnt.collectoralbum.cards.CollectibleCard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public final class CollectibleCardRegistry implements SimpleDataRegistry<Item, CollectibleCard> {

    private final Map<ResourceLocation, CollectibleCard> cardsById = new HashMap<>();
    private final Map<Item, CollectibleCard> cardsByItem = new IdentityHashMap<>();
    private final List<Listener<Item, CollectibleCard>> registryListeners = new ArrayList<>();

    public void clearRegistry() {
        this.cardsById.clear();
        this.cardsByItem.clear();
        this.registryListeners.forEach(Listener::onRegistryReload);
    }

    @Override
    public void registerEntry(Item item, CollectibleCard card) {
        Objects.requireNonNull(item);
        Objects.requireNonNull(card);
        CollectibleCard oldValue = cardsByItem.put(item, card);
        cardsById.put(card.getIdentifier(), card);
        if (oldValue != null) {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
            CollectorsAlbumApi.LOGGER.warn("Detected override of '{}' item in CollectibleCardRegistry, this may be a bug!", Objects.toString(itemId));
        }
        this.registryListeners.forEach(listener -> listener.onEntryRegistered(item, card, oldValue));
    }

    @Override
    public Optional<CollectibleCard> getValue(Item item) {
        return Optional.ofNullable(cardsByItem.get(item));
    }

    @Override
    public void addListener(Listener<Item, CollectibleCard> listener) {
        this.registryListeners.add(listener);
    }

    public int getRegistrySize() {
        return cardsById.size();
    }

    public Optional<CollectibleCard> getValueById(ResourceLocation identifier) {
        return Optional.ofNullable(cardsById.get(identifier));
    }
}
