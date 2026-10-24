package team.tnt.collectorsalbum.common.resource.function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;

public class SumNumberProvider implements NumberProvider {

    public static final MapCodec<SumNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(Codec.DOUBLE, NumberProvider.CODEC).fieldOf("left").forGetter(t -> Either.right(t.left)),
            Codec.either(Codec.DOUBLE, NumberProvider.CODEC).fieldOf("right").forGetter(t -> Either.right(t.right))
    ).apply(instance, SumNumberProvider::new));

    private final NumberProvider left;
    private final NumberProvider right;

    public SumNumberProvider(Either<Double, NumberProvider> left, Either<Double, NumberProvider> right) {
        this.left = left.map(ConstantNumberProvider::new, Function.identity());
        this.right = right.map(ConstantNumberProvider::new, Function.identity());
    }

    @Override
    public <N extends Number> N getNumber(Function<Number, N> mapper) {
        double left = this.left.doubleValue();
        double right = this.right.doubleValue();
        return mapper.apply(left + right);
    }

    @Override
    public MapCodec<? extends NumberProvider> codec() {
        return CODEC;
    }
}
