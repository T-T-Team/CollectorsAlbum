package team.tnt.collectoralbum.common.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import team.tnt.collectoralbum.CollectorsAlbum;

public final class SoundRegistry {

    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CollectorsAlbum.MODID);

    public static final RegistryObject<SoundEvent> FLIP_COMMON = REGISTRY.register("flip_common", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(CollectorsAlbum.MODID, "flip_common"), 16F));
    public static final RegistryObject<SoundEvent> FLIP_UNCOMMON = REGISTRY.register("flip_uncommon", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(CollectorsAlbum.MODID, "flip_uncommon"), 16));
    public static final RegistryObject<SoundEvent> FLIP_RARE = REGISTRY.register("flip_rare", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(CollectorsAlbum.MODID, "flip_rare"), 16));
    public static final RegistryObject<SoundEvent> FLIP_EPIC = REGISTRY.register("flip_epic", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(CollectorsAlbum.MODID, "flip_epic"), 16));
    public static final RegistryObject<SoundEvent> FLIP_LEGENDARY = REGISTRY.register("flip_legendary", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(CollectorsAlbum.MODID, "flip_legendary"), 16));
    public static final RegistryObject<SoundEvent> FLIP_MYTHICAL = REGISTRY.register("flip_mythical", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(CollectorsAlbum.MODID, "flip_mythical"), 16));
    public static final RegistryObject<SoundEvent> OPEN = REGISTRY.register("open", () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(CollectorsAlbum.MODID, "open"), 16));
}
