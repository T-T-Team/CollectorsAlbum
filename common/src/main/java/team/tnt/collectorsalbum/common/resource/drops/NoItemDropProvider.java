package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

import java.util.stream.Stream;

public final class NoItemDropProvider implements ItemDropProvider {

    public static final NoItemDropProvider INSTANCE = new NoItemDropProvider();
    public static final MapCodec<NoItemDropProvider> CODEC = MapCodec.unit(INSTANCE);

    private NoItemDropProvider() {}

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
    }

    @Override
    public MapCodec<? extends ItemDropProvider> codec() {
        return CODEC;
    }

    @Override
    public Stream<ItemStackTemplate> view() {
        return Stream.empty();
    }
}
