package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;

import java.util.function.Function;
import java.util.stream.Stream;

public interface ItemDropProvider {

    Codec<ItemDropProvider> CODEC = CollectorsAlbumRegistries.ITEM_DROP_PROVIDER.byNameCodec().dispatch(ItemDropProvider::codec, Function.identity());

    void generateDrops(ActionContext context, OutputBuilder<ItemStack> output);

    MapCodec<? extends ItemDropProvider> codec();

    Stream<ItemStackTemplate> view();
}
