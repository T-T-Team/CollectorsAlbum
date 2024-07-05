package team.tnt.collectorsalbum.common.resource.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigHolder;
import team.tnt.collectorsalbum.common.init.NumberProviderRegistry;

import java.util.Optional;

public class ConfigValueNumberProvider implements NumberProvider {

    public static final MapCodec<ConfigValueNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("config").forGetter(t -> t.config),
            Codec.STRING.fieldOf("path").forGetter(t -> t.path),
            Codec.INT.optionalFieldOf("defaultValue", 0).forGetter(t -> t.defaultValue)
    ).apply(instance, ConfigValueNumberProvider::new));

    private final String config;
    private final String path;
    private final int defaultValue;

    public ConfigValueNumberProvider(String configId, String path, int defaultValue) {
        this.config = configId;
        this.path = path;
        this.defaultValue = defaultValue;
    }

    @Override
    public int getAsInt() {
        Optional<ConfigHolder<Object>> holder = Configuration.getConfig(this.config);
        return holder.flatMap(cfg -> cfg.getValue(this.path, Integer.class)).orElse(this.defaultValue);
    }

    @Override
    public NumberProviderType<?> getType() {
        return NumberProviderRegistry.CONFIG.get();
    }
}
