package team.tnt.collectorsalbum.common.resource.drops;

import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

public interface ItemDropProvider {

    void generateDrops(ActionContext context, OutputBuilder<ItemStack> output);

    ItemDropProviderType<?> getType();
}
