package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

import java.util.stream.Stream;

public class ItemStackDropProvider implements ItemDropProvider {

    public static final MapCodec<ItemStackDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("itemStack").forGetter(t -> t.itemStack)
    ).apply(instance, ItemStackDropProvider::new));

    private final ItemStackTemplate itemStack;

    public ItemStackDropProvider(ItemStackTemplate itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
        output.accept(this.itemStack.create());
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.ITEMSTACK_DROP_PROVIDER.get();
    }

    @Override
    public Stream<ItemStackTemplate> view() {
        return Stream.of(this.itemStack);
    }
}
