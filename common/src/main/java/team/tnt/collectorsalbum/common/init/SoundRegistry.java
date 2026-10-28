package team.tnt.collectorsalbum.common.init;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import team.tnt.collectorsalbum.platform.registration.Reference;
import team.tnt.collectorsalbum.platform.registration.RegistrationManager;

public final class SoundRegistry {

    public static Reference<SoundEvent> FLIP_COMMON;
    public static Reference<SoundEvent> FLIP_UNCOMMON;
    public static Reference<SoundEvent> FLIP_RARE;
    public static Reference<SoundEvent> FLIP_EPIC;
    public static Reference<SoundEvent> FLIP_LEGENDARY;
    public static Reference<SoundEvent> FLIP_MYTHICAL;
    public static Reference<SoundEvent> PACK_OPEN;

    public static void init(RegistrationManager<SoundEvent> registry) {
        FLIP_COMMON = registry.register("flip_common", SoundRegistry::simpleSound);
        FLIP_UNCOMMON = registry.register("flip_uncommon", SoundRegistry::simpleSound);
        FLIP_RARE = registry.register("flip_rare", SoundRegistry::simpleSound);
        FLIP_EPIC = registry.register("flip_epic", SoundRegistry::simpleSound);
        FLIP_LEGENDARY = registry.register("flip_legendary", SoundRegistry::simpleSound);
        FLIP_MYTHICAL = registry.register("flip_mythical", SoundRegistry::simpleSound);
        PACK_OPEN = registry.register("pack_open", SoundRegistry::simpleSound);
    }

    private static SoundEvent simpleSound(Identifier location) {
        return SoundEvent.createFixedRangeEvent(location, 16);
    }
}
