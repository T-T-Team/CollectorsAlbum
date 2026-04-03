package team.tnt.collectorsalbum.platform.registration;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

class PlatformRegistryImpl<T> implements PlatformRegistry<T> {

    private final Supplier<Registry<T>> registryRef;
    private final String namespace;
    private Map<Identifier, RegistryElement<T, ?>> registeredRefs = new HashMap<>();

    PlatformRegistryImpl(Supplier<Registry<T>> registryRef, String namespace) {
        this.registryRef = registryRef;
        this.namespace = namespace;
    }

    @Override
    public <R extends T> Reference<R> register(String elementId, Supplier<R> ref) {
        Identifier key = Identifier.fromNamespaceAndPath(this.namespace, elementId);
        RegistryElement<T, R> value = new RegistryElement<>(ref);
        if (this.registeredRefs.put(key, value) != null) {
            throw new IllegalArgumentException("Duplicate key: " + key);
        }
        return value;
    }

    @Override
    public <R extends T> Reference<R> register(String elementId, Function<Identifier, R> ref) {
        Identifier key = Identifier.fromNamespaceAndPath(this.namespace, elementId);
        Supplier<R> supplier = () -> ref.apply(key);
        RegistryElement<T, R> value = new RegistryElement<>(supplier);
        if (this.registeredRefs.put(key, value) != null) {
            throw new IllegalArgumentException("Duplicate key: " + key);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends T> void bindRef(BiConsumer<Identifier, Reference<R>> refConsumer) {
        for (Map.Entry<Identifier, RegistryElement<T, ?>> entry : this.registeredRefs.entrySet()) {
            refConsumer.accept(entry.getKey(), (Reference<R>) entry.getValue());
        }
        this.registeredRefs = null;
    }

    @Override
    public void bind() {
        for (Map.Entry<Identifier, RegistryElement<T, ?>> entry : this.registeredRefs.entrySet()) {
            this.bindInternal(entry.getKey(), entry.getValue());
        }
        this.registeredRefs = null;
    }

    @Override
    public boolean is(ResourceKey<?> resourceKey) {
        return this.registryKey().equals(resourceKey);
    }

    @Override
    public ResourceKey<? extends Registry<T>> registryKey() {
        return this.registryRef.get().key();
    }

    private <R extends T> void bindInternal(Identifier identifier, RegistryElement<T, R> element) {
        Registry.register(this.registryRef.get(), identifier, element.get());
    }
}
