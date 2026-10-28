package team.tnt.collectorsalbum.common.resource.function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.CollectorsAlbum;

import java.util.function.Function;
import java.util.function.Predicate;

public interface NumberProvider {

    Codec<NumberProvider> CODEC = CollectorsAlbum.NUMBER_PROVIDER.byNameCodec().dispatch(NumberProvider::codec, Function.identity());
    Codec<Integer> INT_CODEC = createCodec(Codec.INT, NumberProvider::intValue);
    Codec<Integer> POSITIVE_INT_CODEC = validatedCodec(INT_CODEC, n -> n > 0, n -> "Number must be positive! Got: " + n);
    Codec<Float> FLOAT_CODEC = createCodec(Codec.FLOAT, NumberProvider::floatValue);
    Codec<Float> PERCENT_FLOAT_CODEC = validatedCodec(FLOAT_CODEC, n -> n >= 0 && n <= 1, n -> "Number must be in range <0;1>, got " + n);
    Codec<Double> DOUBLE_CODEC = createCodec(Codec.DOUBLE, NumberProvider::doubleValue);

    <N extends Number> N getNumber(Function<Number, N> mapper);

    MapCodec<? extends NumberProvider> codec();

    default int intValue() {
        return getNumber(Number::intValue);
    }

    default float floatValue() {
        return getNumber(Number::floatValue);
    }

    default double doubleValue() {
        return getNumber(Number::doubleValue);
    }

    static <N extends Number> Codec<N> validatedCodec(Codec<N> codec, Predicate<N> validate, Function<N, String> errorMessageProvider) {
        return codec.validate(v -> {
            if (!validate.test(v)) {
                return DataResult.error(() -> errorMessageProvider.apply(v));
            }
            return DataResult.success(v);
        });
    }

    static <N extends Number> Codec<N> createCodec(Codec<N> baseCodec, Function<NumberProvider, N> mapper) {
        return createCodec(baseCodec, mapper, _ -> true);
    }

    static <N extends Number> Codec<N> createCodec(Codec<N> baseCodec, Function<NumberProvider, N> mapper, Predicate<N> validate) {
        return Codec.either(baseCodec, CODEC)
                .xmap(v -> v.map(Function.identity(), mapper), Either::left)
                .validate(v -> {
                    if (!validate.test(v)) {
                        return DataResult.error(() -> "Invalid number input: " + v);
                    }
                    return DataResult.success(v);
                });
    }

    static <N extends Number> Codec<N> rangedCodec(Codec<N> baseCodec, N minValueIncl, N maxValueIncl) {
        return validatedCodec(baseCodec, n -> {
            double v = n.doubleValue();
            return v >= minValueIncl.doubleValue() && v <= maxValueIncl.doubleValue();
        }, n -> "Number must be in range: <" + minValueIncl + ";" + maxValueIncl + ">, got " + n);
    }
}
