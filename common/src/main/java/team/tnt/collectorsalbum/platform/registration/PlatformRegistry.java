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

    static <T> PlatformRegistry<T> create(Reference<T> reference, String namespace) {
        return new PlatformRegistryImpl<>(reference, namespace);
    }

    <R extends T> Supplier<R> register(String elementId, Supplier<R> ref);

    <R extends T> Supplier<R> register(String elementId, Function<ResourceLocation, R> ref);

    <R extends T> void bindRef(BiConsumer<ResourceLocation, Supplier<R>> refConsumer);

    void bind();

    boolean is(ResourceKey<?> resourceKey);

    ResourceKey<? extends Registry<T>> registryKey();

    @FunctionalInterface
    interface Reference<T> extends Supplier<Registry<T>> {

        default Codec<T> codec() {
            return this.get().byNameCodec();
        }
    }
}
