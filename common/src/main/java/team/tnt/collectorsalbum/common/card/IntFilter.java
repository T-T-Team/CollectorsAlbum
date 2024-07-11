package team.tnt.collectorsalbum.common.card;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import team.tnt.collectorsalbum.common.resource.function.ConstantNumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProviderType;

import java.util.function.Function;

public record IntFilter(NumberProvider min, NumberProvider max) {

    public static final MapCodec<IntFilter> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(Codec.INT, NumberProviderType.INSTANCE_CODEC).optionalFieldOf("min", Either.left(Integer.MIN_VALUE)).forGetter(v -> Either.right(v.min)),
            Codec.either(Codec.INT, NumberProviderType.INSTANCE_CODEC).optionalFieldOf("max", Either.left(Integer.MAX_VALUE)).forGetter(v -> Either.right(v.max))
    ).apply(instance, IntFilter::new));
    public static final Codec<IntFilter> CODEC = MAP_CODEC.codec();
    public static final IntFilter NO_FILTER = new IntFilter(new ConstantNumberProvider(Integer.MIN_VALUE), new ConstantNumberProvider(Integer.MAX_VALUE));

    private IntFilter(Either<Integer, NumberProvider> min, Either<Integer, NumberProvider> max) {
        this(min.map(ConstantNumberProvider::new, Function.identity()), max.map(ConstantNumberProvider::new, Function.identity()));
    }

    public boolean test(int value) {
        return value >= min.intValue() && value <= max.intValue();
    }
}
