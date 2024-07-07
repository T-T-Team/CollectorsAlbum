package team.tnt.collectorsalbum.platform.registration;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class FabricRegistration {

    public static <T> void registerCustomRegistry(PlatformRegistry.RegistryReference<T> reference) {
        PlatformRegistryFactory.bindRef(reference, (attributes, binder) -> {
            ResourceKey<Registry<T>> key = attributes.key();
            FabricRegistryBuilder<T, ?> builder = attributes.defaultKey() != null
                    ? FabricRegistryBuilder.createDefaulted(key, attributes.defaultKey())
                    : FabricRegistryBuilder.createSimple(key);
            if (attributes.sync()) {
                builder.attribute(RegistryAttribute.SYNCED);
            }
            Registry<T> registry = builder.buildAndRegister();
            binder.bind(registry);
            CustomPlatformRegistryBindCallback.EVENT.invoker().onBound(registry);
        });
    }
}
