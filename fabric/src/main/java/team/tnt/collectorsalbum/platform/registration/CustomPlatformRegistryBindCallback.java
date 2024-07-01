package team.tnt.collectorsalbum.platform.registration;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.Registry;

public interface CustomPlatformRegistryBindCallback {

    Event<CustomPlatformRegistryBindCallback> EVENT = EventFactory.createArrayBacked(CustomPlatformRegistryBindCallback.class, listeners -> registry -> {
        for (CustomPlatformRegistryBindCallback listener : listeners) {
            listener.onBound(registry);
        }
    });

    void onBound(Registry<?> registry);
}
