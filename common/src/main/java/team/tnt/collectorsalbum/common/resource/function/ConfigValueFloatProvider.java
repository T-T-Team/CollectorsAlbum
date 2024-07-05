package team.tnt.collectorsalbum.common.resource.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigHolder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import team.tnt.collectorsalbum.common.init.NumberProviderRegistry;

import java.util.Optional;

public class ConfigValueFloatProvider implements NumberProvider {

    public static final MapCodec<ConfigValueFloatProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("config").forGetter(t -> t.config),
            Codec.STRING.fieldOf("path").forGetter(t -> t.path),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("precision", 2).forGetter(t -> t.precision),
            Codec.FLOAT.optionalFieldOf("defaultValue", 0.0F).forGetter(t -> t.defaultValue)
    ).apply(instance, ConfigValueFloatProvider::new));

    private final String config;
    private final String path;
    private final int precision;
    private final float defaultValue;

    public ConfigValueFloatProvider(String configId, String path, int precision, float defaultValue) {
        this.config = configId;
        this.path = path;
        this.precision = precision;
        this.defaultValue = defaultValue;
    }

    @Override
    public int getAsInt() {
        Optional<ConfigHolder<Object>> holder = Configuration.getConfig(this.config);
        float value = holder.flatMap(cfg -> cfg.getValue(this.path, Float.class)).orElse(this.defaultValue);
        float unit = (float) Math.pow(10, this.precision);
        return Mth.floor(value * unit);
    }

    @Override
    public NumberProviderType<?> getType() {
        return NumberProviderRegistry.CONFIG_PERCENT.get();
    }
}
