package team.tnt.collectorsalbum.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class MultiLoaderRegistry<T> {

    private final Registry<T> registry;
    private final String namespace;
    private Map<ResourceLocation, RegistryValue<?, ?>> registryObjects = new HashMap<>();

    private MultiLoaderRegistry(Registry<T> registry, String namespace) {
        this.registry = registry;
        this.namespace = namespace;
    }

    public static <T> MultiLoaderRegistry<T> create(Registry<T> registry, String namespace) {
        return new MultiLoaderRegistry<>(registry, namespace);
    }

    public <A extends T> RegistryValue<T, A> register(String name, Function<ResourceLocation, A> provider) {
        assert this.registryObjects != null;
        ResourceLocation key = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
        Supplier<A> supplier = () -> provider.apply(key);
        RegistryValue<T, A> value = new RegistryValue<>(supplier);
        if (this.registryObjects.put(key, value) != null) {
            throw new IllegalArgumentException("Duplicate key: " + key);
        }
        return value;
    }

    public <A extends T> RegistryValue<T, A> register(String name, Supplier<A> provider) {
        assert this.registryObjects != null;
        ResourceLocation key = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
        RegistryValue<T, A> value = new RegistryValue<>(provider);
        if (this.registryObjects.put(key, value) != null) {
            throw new IllegalArgumentException("Duplicate key: " + key);
        }
        return value;
    }

    public <A extends T> void register() {
        for (Map.Entry<ResourceLocation, RegistryValue<?, ?>> entry : this.registryObjects.entrySet()) {
            Registry.register(this.registry, entry.getKey(), (A) entry.getValue().get());
        }
        this.registryObjects = null;
    }

    public <A extends T> void register(BiConsumer<ResourceLocation, Supplier<A>> consumer) {
        for (Map.Entry<ResourceLocation, RegistryValue<?, ?>> entry : this.registryObjects.entrySet()) {
            consumer.accept(entry.getKey(), (Supplier<A>) entry.getValue());
        }
        this.registryObjects = null;
    }

    public ResourceKey<? extends Registry<T>> key() {
        return this.registry.key();
    }
}
