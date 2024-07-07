package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;
import team.tnt.collectorsalbum.common.resource.function.ConstantNumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProviderType;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;
import team.tnt.collectorsalbum.platform.Codecs;

import java.util.function.Function;

public class RandomChanceFilterItemDropProvider implements ItemDropProvider {

    public static final MapCodec<RandomChanceFilterItemDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(Codecs.PERCENT_FLOAT, NumberProviderType.INSTANCE_CODEC).fieldOf("chance").forGetter(t -> Either.right(t.chance)),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("precision", 2).forGetter(t -> t.precision),
            ItemDropProviderType.INSTANCE_CODEC.fieldOf("item").forGetter(t -> t.item)
    ).apply(instance, RandomChanceFilterItemDropProvider::new));

    private final NumberProvider chance;
    private final int precision;
    private final ItemDropProvider item;

    public RandomChanceFilterItemDropProvider(Either<Float, NumberProvider> chance, int precision, ItemDropProvider item) {
        float unit = (float) Math.pow(10, precision);
        this.chance = chance.map(f -> new ConstantNumberProvider(Mth.floor(f * unit)), Function.identity());
        this.precision = precision;
        this.item = item;
    }

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
        RandomSource source = context.getOrThrow(ActionContext.RANDOM, RandomSource.class);
        int unit = (int) Math.pow(10, this.precision);
        int chance = Mth.clamp(this.chance.getAsInt(), 0, unit);
        float percent = chance / (float) unit;
        float roll = source.nextFloat();
        if (roll < percent) {
            this.item.generateDrops(context, output);
        }
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.RANDOM_CHANCE.get();
    }
}
