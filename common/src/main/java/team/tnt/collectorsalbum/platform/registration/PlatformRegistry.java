package team.tnt.collectorsalbum.platform.registration;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface PlatformRegistry<T> {

    static <T> PlatformRegistry<T> create(Registry<T> registry, String namespace) {
        return new PlatformRegistryImpl<>(() -> registry, namespace);
    }

    static <T> PlatformRegistry<T> create(RegistryReference<T> reference, String namespace) {
        return new PlatformRegistryImpl<>(reference, namespace);
    }

    <R extends T> Reference<R> register(String elementId, Supplier<R> ref);

    <R extends T> Reference<R> register(String elementId, Function<ResourceLocation, R> ref);

    <R extends T> void bindRef(BiConsumer<ResourceLocation, Reference<R>> refConsumer);

    void bind();

    boolean is(ResourceKey<?> resourceKey);

    ResourceKey<? extends Registry<T>> registryKey();

    @FunctionalInterface
    interface Reference<T> extends Supplier<T> {
    }

    @FunctionalInterface
    interface RegistryReference<T> extends PlatformRegistry.Reference<Registry<T>> {

        default Codec<T> byNameCodec() {
            return this.get().byNameCodec();
        }
    }
}
