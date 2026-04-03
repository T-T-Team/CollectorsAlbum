package team.tnt.collectorsalbum.common.resource.drops;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

import java.util.stream.Stream;

public interface ItemDropProvider {

    void generateDrops(ActionContext context, OutputBuilder<ItemStack> output);

    ItemDropProviderType<?> getType();

    Stream<ItemStackTemplate> view();
}
