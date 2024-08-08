package team.tnt.collectorsalbum.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class SoundRegistry {

    public static final PlatformRegistry<SoundEvent> REGISTRY = PlatformRegistry.create(BuiltInRegistries.SOUND_EVENT, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<SoundEvent> FLIP_COMMON = REGISTRY.register("flip_common", SoundRegistry::simpleSound);
    public static final PlatformRegistry.Reference<SoundEvent> FLIP_UNCOMMON = REGISTRY.register("flip_uncommon", SoundRegistry::simpleSound);
    public static final PlatformRegistry.Reference<SoundEvent> FLIP_RARE = REGISTRY.register("flip_rare", SoundRegistry::simpleSound);
    public static final PlatformRegistry.Reference<SoundEvent> FLIP_EPIC = REGISTRY.register("flip_epic", SoundRegistry::simpleSound);
    public static final PlatformRegistry.Reference<SoundEvent> FLIP_LEGENDARY = REGISTRY.register("flip_legendary", SoundRegistry::simpleSound);
    public static final PlatformRegistry.Reference<SoundEvent> FLIP_MYTHICAL = REGISTRY.register("flip_mythical", SoundRegistry::simpleSound);
    public static final PlatformRegistry.Reference<SoundEvent> PACK_OPEN = REGISTRY.register("pack_open", SoundRegistry::simpleSound);

    private static SoundEvent simpleSound(ResourceLocation location) {
        return SoundEvent.createFixedRangeEvent(location, 16);
    }
}
