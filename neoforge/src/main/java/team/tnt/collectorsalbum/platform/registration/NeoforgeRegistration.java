package team.tnt.collectorsalbum.platform.registration;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public final class NeoforgeRegistration {

    public static <T> void subscribeRegistryEvent(IEventBus modEventBus, PlatformRegistry<T> registry) {
        modEventBus.addListener(EventPriority.NORMAL, false, RegisterEvent.class, event -> {
            ResourceKey<?> eventKey = event.getRegistryKey();
            if (registry.is(eventKey)) {
                registry.bindRef((identifier, ref) -> event.register(registry.registryKey(), helper -> helper.register(identifier, ref.get())));
            }
        });
    }

    public static <T> void bindNewRegistries(NewRegistryEvent event) {
        PlatformRegistryFactory.<T>bindRefs((attributes, binder) -> {
            ResourceKey<Registry<T>> resourceKey = attributes.key();
            RegistryBuilder<T> builder = new RegistryBuilder<>(resourceKey);
            if (attributes.defaultKey() != null) {
                builder.defaultKey(attributes.defaultKey());
            }
            builder.sync(attributes.sync());
            Registry<T> registry = builder.create();
            event.register(registry);
            binder.bind(registry);
        });
    }

    private NeoforgeRegistration() {}
}
