package team.tnt.collectorsalbum.common.resource.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigHolder;
import team.tnt.collectorsalbum.common.init.NumberProviderRegistry;

import java.util.Optional;
import java.util.function.Function;

public class ConfigValueIntProvider implements NumberProvider {

    public static final MapCodec<ConfigValueIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("config").forGetter(t -> t.config),
            Codec.STRING.fieldOf("path").forGetter(t -> t.path),
            Codec.DOUBLE.optionalFieldOf("defaultValue", 0.0).forGetter(t -> t.defaultValue)
    ).apply(instance, ConfigValueIntProvider::new));

    private final String config;
    private final String path;
    private final double defaultValue;

    public ConfigValueIntProvider(String configId, String path, double defaultValue) {
        this.config = configId;
        this.path = path;
        this.defaultValue = defaultValue;
    }

    @Override
    public <N extends Number> N getNumber(Function<Number, N> mapper) {
        Optional<ConfigHolder<Object>> holder = Configuration.getConfig(this.config);
        Number number = holder.flatMap(cfg -> cfg.getValue(this.path, Number.class)).orElse(this.defaultValue);
        return mapper.apply(number);
    }

    @Override
    public NumberProviderType<?> getType() {
        return NumberProviderRegistry.CONFIG_INT.get();
    }
}
