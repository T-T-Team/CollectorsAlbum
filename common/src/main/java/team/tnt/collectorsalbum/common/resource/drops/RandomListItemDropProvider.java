package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

import java.util.List;

public class RandomListItemDropProvider implements ItemDropProvider {

    public static final MapCodec<RandomListItemDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemDropProviderType.INSTANCE_CODEC.listOf().fieldOf("items").forGetter(t -> t.items)
    ).apply(instance, RandomListItemDropProvider::new));

    private final List<ItemDropProvider> items;

    public RandomListItemDropProvider(List<ItemDropProvider> items) {
        this.items = items;
    }

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
        RandomSource randomSource = context.getOrThrow(ActionContext.RANDOM, RandomSource.class);
        if (items.isEmpty())
            return;
        int index = randomSource.nextInt(items.size());
        ItemDropProvider provider = items.get(index);
        provider.generateDrops(context, output);
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.RANDOM_ITEM_DROP_PROVIDER.get();
    }
}
