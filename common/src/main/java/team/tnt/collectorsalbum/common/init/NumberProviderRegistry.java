package team.tnt.collectorsalbum.common.init;

import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.resource.function.*;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class NumberProviderRegistry {

    public static final PlatformRegistry<NumberProviderType<?>> REGISTRY = PlatformRegistry.create(CollectorsAlbumRegistries.NUMBER_PROVIDER, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<NumberProviderType<ConstantNumberProvider>> CONSTANT = REGISTRY.register("constant", () -> new NumberProviderType<>(ConstantNumberProvider.CODEC));
    public static final PlatformRegistry.Reference<NumberProviderType<RandomNumberProvider>> RANDOM = REGISTRY.register("random", () -> new NumberProviderType<>(RandomNumberProvider.CODEC));
    public static final PlatformRegistry.Reference<NumberProviderType<ConfigValueIntProvider>> CONFIG_INT = REGISTRY.register("config", () -> new NumberProviderType<>(ConfigValueIntProvider.CODEC));
    public static final PlatformRegistry.Reference<NumberProviderType<SumNumberProvider>> SUM = REGISTRY.register("sum", () -> new NumberProviderType<>(SumNumberProvider.CODEC));
}
