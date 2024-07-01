package team.tnt.collectorsalbum.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class Codecs {

    public static final Codec<Float> PERCENT_FLOAT = rangeInclusiveFloat(0.0F, 1.0F);

    public static Codec<Float> rangeInclusiveFloat(float min, float max) {
        return Codec.FLOAT.validate(f -> f >= min && f <= max
                ? DataResult.success(f)
                : DataResult.error(() -> String.format("Value [%f] is not within required range [%f;%f]", f, min, max))
        );
    }

    public static Codec<Float> minFloat(float min) {
        return rangeInclusiveFloat(min, Float.MAX_VALUE);
    }

    public static Codec<Float> maxFloat(float max) {
        return rangeInclusiveFloat(-Float.MAX_VALUE, max);
    }

    public static <E extends Enum<E>> Codec<E> simpleEnumCodec(Class<E> type) {
        return enumCodec(type, s -> Enum.valueOf(type, s), Enum::name);
    }

    public static <E extends Enum<E>> Codec<E> simpleEnumCodec(Class<E> type, UnaryOperator<String> nameTransform) {
        return enumCodec(type, s -> Enum.valueOf(type, nameTransform.apply(s)), Enum::name);
    }

    public static <E extends Enum<E>> Codec<E> enumCodecWithEncoder(Class<E> type, Function<E, String> encoder) {
        return enumCodec(type, s -> Enum.valueOf(type, s), encoder);
    }

    public static <E extends Enum<E>> Codec<E> enumCodecWithDecoder(Class<E> type, Function<String, E> decoder) {
        return enumCodec(type, decoder, Enum::name);
    }

    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> type, Function<String, E> decoder, Function<E, String> encoder) {
        return Codec.STRING.flatXmap(
                key -> {
                    try {
                        E value = decoder.apply(key);
                        return DataResult.success(value);
                    } catch (Exception e) {
                        return DataResult.error(() -> String.format("Failed to decode enum '%s' due to error %s", type.getSimpleName(), e));
                    }
                },
                value -> {
                    try {
                        String key = encoder.apply(value);
                        return DataResult.success(key);
                    } catch (Exception e) {
                        return DataResult.error(() -> String.format("Failed to encode enum to text for provided value '%s' due to error %s", value, e));
                    }
                }
        );
    }
}
