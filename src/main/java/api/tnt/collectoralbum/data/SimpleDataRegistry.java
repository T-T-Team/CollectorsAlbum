package api.tnt.collectoralbum.data;

import javax.annotation.Nullable;
import java.util.Optional;

public interface SimpleDataRegistry<K, V> {

    void registerEntry(K key, V value);

    Optional<V> getValue(K key);

    void addListener(Listener<K, V> listener);

    interface Listener<K, V> {

        default void onRegistryReload() {}

        default void onEntryRegistered(K key, V value, @Nullable V oldValue) {}
    }
}
