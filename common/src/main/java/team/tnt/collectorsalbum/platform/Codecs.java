package team.tnt.collectorsalbum.platform;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
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
    public static final Codec<Holder<Item>> ITEM_NON_AIR_CODEC = Codecs.validate(BuiltInRegistries.ITEM.holderByNameCodec(), item -> item.is(Items.AIR.builtInRegistryHolder().key())
            ? DataResult.error(() -> "Item must not be minecraft:air!")
            : DataResult.success(item)
    );
    public static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(
            string -> {
                try {
                    return DataResult.success(UUID.fromString(string));
                } catch (Exception e) {
                    return DataResult.error(() -> "Invalid UUID: " + string + ", " + e.getMessage());
                }
            }, UUID::toString
    );
    public static final Codec<ItemStack> SINGLE_ITEM_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
            ExtraCodecs.intRange(1, 99).fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(itemStack -> Optional.ofNullable(itemStack.getTag()))
    ).apply(instance, (holder, count, tag) -> {
        ItemStack itemStack = new ItemStack(holder, count);
        tag.ifPresent(itemStack::setTag);
        return itemStack;
    }));
    public static final Codec<ItemStack> SIMPLE_ITEM_CODEC = ITEM_NON_AIR_CODEC.xmap(ItemStack::new, ItemStack::getItemHolder);

    public static <T> Codec<T> validate(Codec<T> codec, Function<T, DataResult<T>> validator) {
        return codec.flatXmap(validator, validator);
    }

    public static <T> MapCodec<T> validate(MapCodec<T> codec, Function<T, DataResult<T>> validator) {
        return codec.flatXmap(validator, validator);
    }

    public static Codec<Float> rangeInclusiveFloat(float min, float max) {
        return validate(Codec.FLOAT, f -> f >= min && f <= max
                ? DataResult.success(f)
                : DataResult.error(() -> String.format("Value [%f] is not within required range [%f;%f]", f, min, max))
        );
    }

    public static <T> Codec<T[]> array(Codec<T> codec, IntFunction<T[]> toArray) {
        return array(codec, toArray, Arrays::asList);
    }

    public static <T> Codec<T[]> array(Codec<T> codec, IntFunction<T[]> toArray, Function<T[], List<T>> toList) {
        return codec.listOf().xmap(
                list -> {
                    T[] values = toArray.apply(list.size());
                    for (int i = 0; i < list.size(); i++) {
                        values[i] = list.get(i);
                    }
                    return values;
                }, toList
        );
    }

    public static <T> Codec<Supplier<T>> supplier(Codec<T> codec) {
        return codec.xmap(
                t -> () -> t,
                Supplier::get
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
