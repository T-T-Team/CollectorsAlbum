package team.tnt.collectorsalbum.platform.registration;

import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class NeoforgeRegistration {

    public static <T> void subscribeRegistryEvent(IEventBus modEventBus, PlatformRegistry<T> registry) {
        modEventBus.addListener(EventPriority.NORMAL, false, RegisterEvent.class, event -> {
            ResourceKey<?> eventKey = event.getRegistryKey();
            if (registry.is(eventKey)) {
                registry.bindRef((identifier, ref) -> event.register(registry.registryKey(), helper -> helper.register(identifier, ref.get())));
            }
        });
    }

    private NeoforgeRegistration() {}
}
