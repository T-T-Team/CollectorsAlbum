package team.tnt.collectorsalbum.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

import java.util.function.Supplier;

public final class SoundRegistry {

    public static final PlatformRegistry<SoundEvent> REGISTRY = PlatformRegistry.create(BuiltInRegistries.SOUND_EVENT, CollectorsAlbum.MOD_ID);

    public static final Supplier<SoundEvent> FLIP_COMMON = REGISTRY.register("flip_common", SoundRegistry::flipSound);
    public static final Supplier<SoundEvent> FLIP_UNCOMMON = REGISTRY.register("flip_uncommon", SoundRegistry::flipSound);
    public static final Supplier<SoundEvent> FLIP_RARE = REGISTRY.register("flip_rare", SoundRegistry::flipSound);
    public static final Supplier<SoundEvent> FLIP_EPIC = REGISTRY.register("flip_epic", SoundRegistry::flipSound);
    public static final Supplier<SoundEvent> FLIP_LEGENDARY = REGISTRY.register("flip_legendary", SoundRegistry::flipSound);
    public static final Supplier<SoundEvent> FLIP_MYTHICAL = REGISTRY.register("flip_mythical", SoundRegistry::flipSound);

    private static SoundEvent flipSound(ResourceLocation location) {
        return SoundEvent.createFixedRangeEvent(location, 16);
    }
}
