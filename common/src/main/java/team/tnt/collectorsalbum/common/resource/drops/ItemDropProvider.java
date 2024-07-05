package team.tnt.collectorsalbum.common.resource.drops;

import net.minecraft.world.item.ItemStack;

public interface ItemDropProvider {

    void generateDrops(DropContext context, DropOutputBuilder<ItemStack> output);

    ItemDropProviderType<?> getType();
}
