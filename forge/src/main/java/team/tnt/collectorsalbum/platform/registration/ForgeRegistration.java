package team.tnt.collectorsalbum.platform.registration;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;

public final class ForgeRegistration {

    public static <T> void subscribeRegistryEvent(IEventBus modEventBus, PlatformRegistry<T> registry) {
        modEventBus.addListener(EventPriority.NORMAL, false, RegisterEvent.class, event -> {
            ResourceKey<?> eventKey = event.getRegistryKey();
            if (registry.is(eventKey)) {
                registry.bindRef((identifier, ref) -> event.register(registry.registryKey(), helper -> helper.register(identifier, ref.get())));
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> void bindCustomRegistries(NewRegistryEvent event) {
        PlatformRegistryFactory.<T>bindRefs((attributes, binder) -> {
            ResourceKey<Registry<T>> resourceKey = attributes.key();
            ResourceLocation key = resourceKey.location();
            RegistryBuilder<T> builder = RegistryBuilder.of(key);
            if (!attributes.sync()) {
                builder.disableSync();
            }
            builder.hasTags();
            event.create(builder, ifr -> {
                Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(key);
                binder.bind(registry);
            });
        });
    }

    private ForgeRegistration() {}
}
