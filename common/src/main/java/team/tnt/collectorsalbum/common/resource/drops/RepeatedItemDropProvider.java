package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;
import team.tnt.collectorsalbum.common.resource.function.ConstantNumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProviderType;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

import java.util.function.Function;

public class RepeatedItemDropProvider implements ItemDropProvider {

    public static final MapCodec<RepeatedItemDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(ExtraCodecs.POSITIVE_INT, NumberProviderType.INSTANCE_CODEC).fieldOf("times").forGetter(t -> Either.right(t.times)),
            ItemDropProviderType.INSTANCE_CODEC.fieldOf("item").forGetter(t -> t.item)
    ).apply(instance, RepeatedItemDropProvider::new));

    private final NumberProvider times;
    private final ItemDropProvider item;

    public RepeatedItemDropProvider(Either<Integer, NumberProvider> timesEither, ItemDropProvider item) {
        this.times = timesEither.map(ConstantNumberProvider::new, Function.identity());
        this.item = item;
    }

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
        int count = this.times.intValue();
        for (int i = 0; i < count; i++) {
            this.item.generateDrops(context, output);
        }
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.REPEATED_DROP_PROVIDER.get();
    }
}
