package team.tnt.collectorsalbum.common.resource.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigValueLocation;
import team.tnt.collectorsalbum.common.init.NumberProviderRegistry;

import java.util.Optional;
import java.util.function.Function;

public class ConfigValueIntProvider implements NumberProvider {

    public static final MapCodec<ConfigValueIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ConfigValueLocation.CODEC.fieldOf("location").forGetter(t -> t.location),
            Codec.DOUBLE.optionalFieldOf("defaultValue", 0.0).forGetter(t -> t.defaultValue)
    ).apply(instance, ConfigValueIntProvider::new));

    private final ConfigValueLocation location;
    private final double defaultValue;

    public ConfigValueIntProvider(ConfigValueLocation location, double defaultValue) {
        this.location = location;
        this.defaultValue = defaultValue;
    }

    @Override
    public <N extends Number> N getNumber(Function<Number, N> mapper) {
        Optional<Number> value = Configuration.getConfigValue(this.location, Number.class);
        Number number = value.orElse(this.defaultValue);
        return mapper.apply(number);
    }

    @Override
    public NumberProviderType<?> getType() {
        return NumberProviderRegistry.CONFIG_INT.get();
    }
}
