package team.tnt.collectorsalbum.common.init;

import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.resource.function.*;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class NumberProviderRegistry {

    public static final PlatformRegistry<MapCodec<? extends NumberProvider>> REGISTRY = PlatformRegistry.create(CollectorsAlbumRegistries.NUMBER_PROVIDER, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<MapCodec<ConstantNumberProvider>> CONSTANT = REGISTRY.registerInstance("constant", ConstantNumberProvider.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<RandomNumberProvider>> RANDOM = REGISTRY.registerInstance("random", RandomNumberProvider.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<ConfigValueIntProvider>> CONFIG_INT = REGISTRY.registerInstance("config", ConfigValueIntProvider.CODEC);
    public static final PlatformRegistry.Reference<MapCodec<SumNumberProvider>> SUM = REGISTRY.registerInstance("sum", SumNumberProvider.CODEC);
}
