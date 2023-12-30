package api.tnt.collectoralbum.data;

import api.tnt.collectoralbum.CollectorsAlbumApi;
import api.tnt.collectoralbum.cards.CardGroup;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class CardGroupRegistry implements SimpleDataRegistry<ResourceLocation, CardGroup> {

    private final Map<ResourceLocation, CardGroup> values = new HashMap<>();
    private final List<Listener<ResourceLocation, CardGroup>> listeners = new ArrayList<>();
    private Map<ResourceLocation, CardGroup> preInitializedValues;
    private boolean completedLoading;

    public void clearRegistry() {
        this.setLoadingState(true);
        this.preInitializedValues = new HashMap<>();
        this.values.clear();
        this.listeners.forEach(Listener::onRegistryReload);
    }

    @Override
    public void registerEntry(ResourceLocation key, CardGroup value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        CardGroup oldValue = values.put(key, value);
        if (oldValue != null) {
            CollectorsAlbumApi.LOGGER.warn("Detected override of '{}' item in CollectibleCardRegistry, this may be a bug!", oldValue.getIdentifier());
        }
        this.listeners.forEach(listener -> listener.onEntryRegistered(key, value, oldValue));
    }

    @Override
    public Optional<CardGroup> getValue(ResourceLocation key) {
        return Optional.ofNullable(completedLoading ? values.get(key) : preInitializedValues.get(key));
    }

    @Override
    public void addListener(Listener<ResourceLocation, CardGroup> listener) {
        this.listeners.add(listener);
    }

    public Map<ResourceLocation, CardGroup> getPreparedGroups() {
        return preInitializedValues;
    }

    public void setLoadingState(boolean loading) {
        this.completedLoading = !loading;
    }

    public void clearLoadedData() {
        this.preInitializedValues = null;
    }

    public void savePreloadedGroup(ResourceLocation location, CardGroup group) {
        Objects.requireNonNull(location);
        Objects.requireNonNull(group);
        CardGroup oldValue = preInitializedValues.put(location, group);
        if (oldValue != null) {
            CollectorsAlbumApi.LOGGER.warn("Detected override of '{}' item in CollectibleCardRegistry, this may be a bug!", oldValue.getIdentifier());
        }
    }
}
