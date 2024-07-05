package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;

public class ItemStackDropProvider implements ItemDropProvider {

    public static final MapCodec<ItemStackDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("itemStack").forGetter(t -> t.itemStack)
    ).apply(instance, ItemStackDropProvider::new));

    private final ItemStack itemStack;

    public ItemStackDropProvider(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void generateDrops(DropContext context, DropOutputBuilder<ItemStack> output) {
        output.accept(this.itemStack.copy());
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.ITEMSTACK_DROP_PROVIDER.get();
    }
}
