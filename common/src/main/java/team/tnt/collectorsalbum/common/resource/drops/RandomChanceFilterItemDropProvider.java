package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

import java.util.stream.Stream;

public class RandomChanceFilterItemDropProvider implements ItemDropProvider {

    public static final MapCodec<RandomChanceFilterItemDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProvider.PERCENT_FLOAT_CODEC.fieldOf("chance").forGetter(t -> t.chance),
            ItemDropProvider.CODEC.fieldOf("item").forGetter(t -> t.item)
    ).apply(instance, RandomChanceFilterItemDropProvider::new));

    private final float chance;
    private final ItemDropProvider item;

    public RandomChanceFilterItemDropProvider(float chance, ItemDropProvider item) {
        this.chance = chance;
        this.item = item;
    }

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
        RandomSource source = context.getOrThrow(ActionContext.RANDOM, RandomSource.class);
        float roll = source.nextFloat();
        if (roll < this.chance) {
            this.item.generateDrops(context, output);
        }
    }

    @Override
    public MapCodec<? extends ItemDropProvider> codec() {
        return CODEC;
    }

    @Override
    public Stream<ItemStackTemplate> view() {
        return this.chance > 0.0F ? this.item.view() : Stream.empty();
    }
}
