package team.tnt.collectorsalbum.common.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ItemBlockHelper {

    private final MultiLoaderRegistry<Item> itemRegistry;

    public ItemBlockHelper(MultiLoaderRegistry<Item> itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public Supplier<BlockItem> registerItemBlock(Supplier<? extends Block> blockHolder) {
        return this.registerItemBlock(blockHolder, block -> new BlockItem(block, new Item.Properties()));
    }

    public <T extends BlockItem> Supplier<T> registerItemBlock(Supplier<? extends Block> blockHolder, Function<Block, T> factory) {
        Block block = blockHolder.get();
        ResourceLocation identifier = BuiltInRegistries.BLOCK.getKey(block);
        return this.itemRegistry.register(identifier.getPath(), () -> factory.apply(block));
    }
}
