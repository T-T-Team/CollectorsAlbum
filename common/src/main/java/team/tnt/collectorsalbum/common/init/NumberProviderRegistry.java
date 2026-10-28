package team.tnt.collectorsalbum.common.init;

import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.common.resource.function.*;
import team.tnt.collectorsalbum.platform.registration.RegistrationManager;

public final class NumberProviderRegistry {

    public static void init(RegistrationManager<MapCodec<? extends NumberProvider>> registry) {
        registry.registerInstance("constant", ConstantNumberProvider.CODEC);
        registry.registerInstance("random", RandomNumberProvider.CODEC);
        registry.registerInstance("config", ConfigValueIntProvider.CODEC);
        registry.registerInstance("sum", SumNumberProvider.CODEC);
    }
}
