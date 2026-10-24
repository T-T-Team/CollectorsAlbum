package team.tnt.collectorsalbum.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;

import java.util.List;
import java.util.Optional;
import java.util.Random;
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

    public List<WeightedItem<T>> getEntries() {
        return items;
    }

    private int getTotal() {
        return this.items.stream().mapToInt(WeightedItem::getWeight).sum();
    }

    public record WeightedItem<T>(int weight, T item) {

        public static <T> WeightedItem<T> decode(int weight, T item) {
            return new WeightedItem<>(weight, item);
        }

        public int getWeight() {
            return this.weight;
        }

        public static <T> Codec<WeightedItem<T>> codec(Codec<T> elementCodec) {
            return RecordCodecBuilder.create(instance -> instance.group(
                    NumberProvider.POSITIVE_INT_CODEC.fieldOf("weight").forGetter(item -> item.weight),
                    elementCodec.fieldOf("item").forGetter(t -> t.item)
            ).apply(instance, WeightedItem::decode));
        }
    }
}
