package team.tnt.collectorsalbum.common.resource.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import team.tnt.collectorsalbum.common.init.NumberProviderRegistry;
import team.tnt.collectorsalbum.platform.Codecs;

import java.util.Random;
import java.util.function.Function;

public class RandomNumberProvider implements NumberProvider {

    public static final MapCodec<RandomNumberProvider> CODEC = Codecs.validate(RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("min").forGetter(t -> t.min),
            Codec.DOUBLE.fieldOf("max").forGetter(t -> t.max)
    ).apply(instance, RandomNumberProvider::new)), o -> o.min > o.max ? DataResult.error(() -> "Min value cannot be larger than max value!") : DataResult.success(o));
    private static final Random RANDOM = new Random();
    private final double min;
    private final double max;

    public RandomNumberProvider(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public <N extends Number> N getNumber(Function<Number, N> mapper) {
        double delta = max - min;
        double randomBetween = min + delta * RANDOM.nextDouble();
        return mapper.apply(randomBetween);
    }

    @Override
    public NumberProviderType<?> getType() {
        return NumberProviderRegistry.RANDOM.get();
    }
}
