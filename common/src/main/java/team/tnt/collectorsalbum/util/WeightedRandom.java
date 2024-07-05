package team.tnt.collectorsalbum.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import team.tnt.collectorsalbum.common.resource.function.ConstantNumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProviderType;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class WeightedRandom<T> implements Supplier<T> {

    private final Random random = new Random();
    private final List<WeightedItem<T>> items;
    private final T defaultValue;
    private Integer total;

    public WeightedRandom(List<WeightedItem<T>> items, T defaultValue) {
        this.items = items;
        this.defaultValue = defaultValue;
    }

    public WeightedRandom(List<WeightedItem<T>> items) {
        this(items, null);
    }

    public void setSeed(long seed) {
        this.random.setSeed(seed);
    }

    public static <T> Codec<WeightedRandom<T>> codec(Codec<T> elementCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                WeightedItem.codec(elementCodec).listOf().fieldOf("items").forGetter(t -> t.items),
                elementCodec.optionalFieldOf("defaultValue").forGetter(t -> Optional.ofNullable(t.defaultValue))
        ).apply(instance, (items, defaultValueOptional) -> new WeightedRandom<>(items, defaultValueOptional.orElse(null))));
    }

    @Override
    public T get() {
        if (total == null) {
            total = getTotal();
        }
        if (total > 0) {
            int roll = random.nextInt(total);
            for (WeightedItem<T> item : items) {
                int itemWeight = item.getWeight();
                if ((roll -= itemWeight) <= 0) {
                    return item.item;
                }
            }
        }
        return defaultValue;
    }

    private int getTotal() {
        return this.items.stream().mapToInt(WeightedItem::getWeight).sum();
    }

    public record WeightedItem<T>(NumberProvider weight, T item) {

        public static <T> WeightedItem<T> decode(Either<Integer, NumberProvider> weight, T item) {
            return new WeightedItem<>(weight.map(num -> ConstantNumberProvider.ONE, Function.identity()), item);
        }

        public int getWeight() {
            return this.weight.getAsInt();
        }

        public static <T> Codec<WeightedItem<T>> codec(Codec<T> elementCodec) {
            return RecordCodecBuilder.create(instance -> instance.group(
                    Codec.either(
                            Codec.INT,
                            NumberProviderType.INSTANCE_CODEC
                    ).fieldOf("weight").forGetter(item -> Either.right(item.weight)),
                    elementCodec.fieldOf("item").forGetter(t -> t.item)
            ).apply(instance, WeightedItem::decode));
        }
    }
}
