package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;
import team.tnt.collectorsalbum.util.WeightedRandom;

public class WeightedItemDropProvider implements ItemDropProvider {

    public static final MapCodec<WeightedItemDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WeightedRandom.codec(ItemDropProviderType.INSTANCE_CODEC).fieldOf("value").forGetter(t -> t.items)
    ).apply(instance, WeightedItemDropProvider::new));

    private final WeightedRandom<ItemDropProvider> items;

    public WeightedItemDropProvider(WeightedRandom<ItemDropProvider> items) {
        this.items = items;
    }

    @Override
    public void generateDrops(DropContext context, DropOutputBuilder<ItemStack> output) {
        ItemDropProvider item = items.get();
        if (item != null) {
            item.generateDrops(context, output);
        }
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.WEIGHTED_DROP_PROVIDER.get();
    }
}
