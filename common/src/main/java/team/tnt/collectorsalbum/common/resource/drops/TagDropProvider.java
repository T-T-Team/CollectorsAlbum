package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;
import team.tnt.collectorsalbum.util.TagHelper;

public class TagDropProvider implements ItemDropProvider {

    public static final MapCodec<TagDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(t -> t.tagKey)
    ).apply(instance, TagDropProvider::new));

    private final TagKey<Item> tagKey;

    public TagDropProvider(TagKey<Item> tagKey) {
        this.tagKey = tagKey;
    }

    @Override
    public void generateDrops(DropContext context, DropOutputBuilder<ItemStack> output) {
        RandomSource random = context.getOrThrow(DropContext.RANDOM, RandomSource.class);
        Item item = TagHelper.getRandomTagValue(this.tagKey, BuiltInRegistries.ITEM, random);
        if (item != null) {
            output.accept(new ItemStack(item));
        } else {
            CollectorsAlbum.LOGGER.warn("Failed to obtain items for tag {} for drop generation", this.tagKey);
        }
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.TAG_DROP_PROVIDER.get();
    }
}
