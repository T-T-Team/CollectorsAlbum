package team.tnt.collectorsalbum.platform;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class Codecs {

    public static final Codec<Float> PERCENT_FLOAT = rangeInclusiveFloat(0.0F, 1.0F);
    public static final Codec<Integer> COLOR_CODEC = Codec.either(Codec.INT, Codec.STRING.comapFlatMap(
            text -> {
                try {
                    int color = Integer.decode(text);
                    return DataResult.success(color);
                } catch (NumberFormatException e) {
                    return DataResult.error(e::getMessage);
                }
            },
            Integer::toHexString
    )).xmap(
            either -> either.map(Function.identity(), Function.identity()),
            Either::left
    );

    public static Codec<Float> rangeInclusiveFloat(float min, float max) {
        return Codec.FLOAT.validate(f -> f >= min && f <= max
                ? DataResult.success(f)
                : DataResult.error(() -> String.format("Value [%f] is not within required range [%f;%f]", f, min, max))
        );
    }

    public static <T, C extends Collection<T>> DataResult<C> requireSize(C collection, int size) {
        return collection.size() != size ? DataResult.error(() -> String.format("Collection must contain exactly %d elements, found %d!", size, collection.size())) : DataResult.success(collection);
    }

    public static <T> Codec<Set<T>> setCodec(Codec<T> codec) {
        return setCodec(codec, HashSet::new);
    }

    public static <T> Codec<Set<T>> setCodec(Codec<T> codec, Function<List<T>, Set<T>> codecFactory) {
        return codec.listOf().xmap(codecFactory, ArrayList::new);
    }

    public static <T> Codec<NonNullList<T>> nonNullListCodec(Codec<T> codec, T empty) {
        return codec.listOf().xmap(
                list -> {
                    NonNullList<T> nonNullList = NonNullList.<T>withSize(list.size(), empty);
                    for (int i = 0; i < list.size(); ++i) {
                        nonNullList.set(i, list.get(i));
                    }
                    return nonNullList;
                },
                Function.identity()
        );
    }

    public static <BUF extends FriendlyByteBuf, T> StreamCodec<BUF, NonNullList<T>> nonNullListStreamCodec(StreamCodec<BUF, T> codec, Predicate<T> skip, T empty) {
        return StreamCodec.of(
                (buf, list) -> {
                    for (T t : list) {
                        boolean saved = !skip.test(t);
                        buf.writeBoolean(saved);
                        if (saved) {
                            codec.encode(buf, t);
                        }
                    }
                }, (buf) -> {
                    int size = buf.readInt();
                    NonNullList<T> list = NonNullList.withSize(size, empty);
                    for (int i = 0; i < size; i++) {
                        boolean saved = buf.readBoolean();
                        if (saved) {
                            T t = codec.decode(buf);
                            list.set(i, t);
                        }
                    }
                    return list;
                }
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
