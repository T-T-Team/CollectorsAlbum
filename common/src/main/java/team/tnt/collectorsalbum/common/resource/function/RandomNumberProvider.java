package team.tnt.collectorsalbum.common.resource.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import team.tnt.collectorsalbum.common.init.NumberProviderRegistry;

import java.util.Random;

public class RandomNumberProvider implements NumberProvider {

    public static final MapCodec<RandomNumberProvider> CODEC = RecordCodecBuilder.<RandomNumberProvider>mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("min").forGetter(t -> t.min),
            Codec.INT.fieldOf("max").forGetter(t -> t.max)
    ).apply(instance, RandomNumberProvider::new))
            .validate(o -> o.min > o.max ? DataResult.error(() -> "Min value cannot be larger than max value!") : DataResult.success(o));
    private static final Random RANDOM = new Random();
    private final int min;
    private final int max;

    public RandomNumberProvider(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public int getAsInt() {
        int diff = max - min + 1;
        return RANDOM.nextInt(diff) + min;
    }

    @Override
    public NumberProviderType<?> getType() {
        return NumberProviderRegistry.RANDOM.get();
    }
}
