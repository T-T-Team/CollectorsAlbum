package team.tnt.collectorsalbum.platform.registration;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public final class PlatformRegistryFactory {

    private static List<BindableRegistryReference<?>> PREPARED_REGISTRIES = new ArrayList<>();

    public static <T> PlatformRegistry.Reference<T> createSimple(ResourceKey<Registry<T>> key, boolean sync) {
        RegistryAttributes<T> attributes = new RegistryAttributes<>(key, null, sync);
        BindableRegistryReference<T> reference = new BindableRegistryReference<>(attributes);
        PREPARED_REGISTRIES.add(reference);
        return reference;
    }

    public static <T> PlatformRegistry.Reference<T> createSimple(ResourceKey<Registry<T>> key) {
        return createSimple(key, true);
    }

    @SuppressWarnings("unchecked")
    public static <T> void bindRefs(BiConsumer<RegistryAttributes<T>, RegistryBinder<T>> builder) {
        for (BindableRegistryReference<?> reference : PREPARED_REGISTRIES) {
            bindRef((BindableRegistryReference<T>) reference, builder);
        }
        PREPARED_REGISTRIES = null;
    }

    public static <T> void bindRef(PlatformRegistry.Reference<T> reference, BiConsumer<RegistryAttributes<T>, RegistryBinder<T>> builder) {
        BindableRegistryReference<T> bindable = (BindableRegistryReference<T>) reference;
        RegistryAttributes<T> attributes = bindable.attributes;
        builder.accept(attributes, bindable::bind);
    }

    @FunctionalInterface
    public interface RegistryBinder<T> {
        void bind(Registry<T> registry);
    }

    public record RegistryAttributes<T>(ResourceKey<Registry<T>> key, ResourceLocation defaultKey, boolean sync) { }

    private static final class BindableRegistryReference<T> implements PlatformRegistry.Reference<T> {

        private final RegistryAttributes<T> attributes;
        private Registry<T> instance;

        public BindableRegistryReference(RegistryAttributes<T> attributes) {
            this.attributes = attributes;
        }

        @Override
        public Registry<T> get() {
            if (instance == null)
                throw new IllegalStateException("Cannot obtain registry instance before registration");
            return instance;
        }

        public void bind(Registry<T> instance) {
            this.instance = instance;
        }
    }
}
