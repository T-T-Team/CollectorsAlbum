package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

import java.util.stream.Stream;

public class RepeatedItemDropProvider implements ItemDropProvider {

    public static final MapCodec<RepeatedItemDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProvider.POSITIVE_INT_CODEC.fieldOf("times").forGetter(t -> t.times),
            ItemDropProvider.CODEC.fieldOf("item").forGetter(t -> t.item)
    ).apply(instance, RepeatedItemDropProvider::new));

    private final int times;
    private final ItemDropProvider item;

    public RepeatedItemDropProvider(int times, ItemDropProvider item) {
        this.times = times;
        this.item = item;
    }

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
        for (int i = 0; i < this.times; i++) {
            this.item.generateDrops(context, output);
        }
    }

    @Override
    public MapCodec<? extends ItemDropProvider> codec() {
        return CODEC;
    }

    @Override
    public Stream<ItemStackTemplate> view() {
        return this.item.view();
    }
}
