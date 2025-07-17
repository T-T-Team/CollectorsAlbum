package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;
import team.tnt.collectorsalbum.util.WeightedRandom;

import java.util.stream.Stream;

public class WeightedItemDropProvider implements ItemDropProvider {

    public static final MapCodec<WeightedItemDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WeightedRandom.codec(ItemDropProviderType.INSTANCE_CODEC).fieldOf("value").forGetter(t -> t.items)
    ).apply(instance, WeightedItemDropProvider::new));

    private final WeightedRandom<ItemDropProvider> items;

    public WeightedItemDropProvider(WeightedRandom<ItemDropProvider> items) {
        this.items = items;
    }

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
        ItemDropProvider item = items.get();
        if (item != null) {
            item.generateDrops(context, output);
        }
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.WEIGHTED_DROP_PROVIDER.get();
    }

    @Override
    public Stream<ItemStack> view() {
        return this.items.getEntries().stream()
                .filter(item -> item.getWeight() > 0)
                .map(WeightedRandom.WeightedItem::item)
                .flatMap(ItemDropProvider::view);
    }
}
