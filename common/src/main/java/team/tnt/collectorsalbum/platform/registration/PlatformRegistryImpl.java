package team.tnt.collectorsalbum.platform.registration;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

final class PlatformRegistryImpl<T> implements PlatformRegistry<T> {

    private final Registry<T> registry;
    private final String namespace;
    private Map<ResourceLocation, RegistryElement<T, ?>> registeredRefs = new HashMap<>();

    PlatformRegistryImpl(Registry<T> registry, String namespace) {
        this.registry = registry;
        this.namespace = namespace;
    }

    @Override
    public <R extends T> Supplier<R> register(String elementId, Supplier<R> ref) {
        assert this.registeredRefs != null;
        ResourceLocation key = ResourceLocation.fromNamespaceAndPath(this.namespace, elementId);
        RegistryElement<T, R> value = new RegistryElement<>(ref);
        if (this.registeredRefs.put(key, value) != null) {
            throw new IllegalArgumentException("Duplicate key: " + key);
        }
        return value;
    }

    @Override
    public <R extends T> Supplier<R> register(String elementId, Function<ResourceLocation, R> ref) {
        assert this.registeredRefs != null;
        ResourceLocation key = ResourceLocation.fromNamespaceAndPath(this.namespace, elementId);
        Supplier<R> supplier = () -> ref.apply(key);
        RegistryElement<T, R> value = new RegistryElement<>(supplier);
        if (this.registeredRefs.put(key, value) != null) {
            throw new IllegalArgumentException("Duplicate key: " + key);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends T> void bindRef(BiConsumer<ResourceLocation, Supplier<R>> refConsumer) {
        assert this.registeredRefs != null;
        for (Map.Entry<ResourceLocation, RegistryElement<T, ?>> entry : this.registeredRefs.entrySet()) {
            refConsumer.accept(entry.getKey(), (Supplier<R>) entry.getValue());
        }
        this.registeredRefs = null;
    }

    @Override
    public void bind() {
        assert this.registeredRefs != null;
        for (Map.Entry<ResourceLocation, RegistryElement<T, ?>> entry : this.registeredRefs.entrySet()) {
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
        return this.registry.key();
    }

    private <R extends T> void bindInternal(ResourceLocation identifier, RegistryElement<T, R> element) {
        Registry.register(this.registry, identifier, element.get());
    }
}
