package team.tnt.collectorsalbum.common.resource.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import team.tnt.collectorsalbum.common.init.NumberProviderRegistry;

import java.util.function.Function;

public class ConstantNumberProvider implements NumberProvider {

    public static final MapCodec<ConstantNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("value").forGetter(t -> t.value)
    ).apply(instance, ConstantNumberProvider::new));

    public static final ConstantNumberProvider ZERO = new ConstantNumberProvider(0);
    public static final ConstantNumberProvider ONE = new ConstantNumberProvider(1);

    private final double value;

    public ConstantNumberProvider(double value) {
        this.value = value;
    }

    @Override
    public <N extends Number> N getNumber(Function<Number, N> mapper) {
        return mapper.apply(value);
    }

    @Override
    public NumberProviderType<?> getType() {
        return NumberProviderRegistry.CONSTANT.get();
    }
}
