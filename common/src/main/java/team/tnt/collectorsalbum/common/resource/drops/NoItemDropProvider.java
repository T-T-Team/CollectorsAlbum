package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

public final class NoItemDropProvider implements ItemDropProvider {

    public static final NoItemDropProvider INSTANCE = new NoItemDropProvider();
    public static final MapCodec<NoItemDropProvider> CODEC = MapCodec.unit(INSTANCE);

    private NoItemDropProvider() {}

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.NO_DROP_PROVIDER.get();
    }
}
