package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

import java.util.List;

public class ListItemDropProvider implements ItemDropProvider {

    public static final MapCodec<ListItemDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemDropProviderType.INSTANCE_CODEC.listOf().fieldOf("items").forGetter(t -> t.items)
    ).apply(instance, ListItemDropProvider::new));

    private final List<ItemDropProvider> items;

    public ListItemDropProvider(List<ItemDropProvider> items) {
        this.items = items;
    }

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
        for (ItemDropProvider item : items) {
            item.generateDrops(context, output);
        }
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.LIST_DROP_PROVIDER.get();
    }
}
