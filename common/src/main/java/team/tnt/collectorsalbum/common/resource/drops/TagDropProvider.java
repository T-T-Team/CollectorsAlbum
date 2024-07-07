package team.tnt.collectorsalbum.common.resource.drops;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.init.ItemDropProviderRegistry;
import team.tnt.collectorsalbum.common.resource.function.ConstantNumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProviderType;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.OutputBuilder;
import team.tnt.collectorsalbum.util.TagHelper;

import java.util.function.Function;

public class TagDropProvider implements ItemDropProvider {

    public static final MapCodec<TagDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(t -> t.tagKey),
            Codec.either(ExtraCodecs.POSITIVE_INT, NumberProviderType.INSTANCE_CODEC).optionalFieldOf("count", Either.left(1)).forGetter(t -> Either.right(t.size))
    ).apply(instance, TagDropProvider::new));

    private final TagKey<Item> tagKey;
    private final NumberProvider size;

    public TagDropProvider(TagKey<Item> tagKey, Either<Integer, NumberProvider> size) {
        this.tagKey = tagKey;
        this.size = size.map(ConstantNumberProvider::new, Function.identity());
    }

    @Override
    public void generateDrops(ActionContext context, OutputBuilder<ItemStack> output) {
        RandomSource random = context.getOrThrow(ActionContext.RANDOM, RandomSource.class);
        Item item = TagHelper.getRandomTagValue(this.tagKey, BuiltInRegistries.ITEM, random);
        if (item != null) {
            int sizeUpperBound = item.getDefaultMaxStackSize();
            int itemSize = Mth.clamp(this.size.intValue(), 1, sizeUpperBound);
            output.accept(new ItemStack(item, itemSize));
        } else {
            CollectorsAlbum.LOGGER.warn("Failed to obtain items for tag {} for drop generation", this.tagKey);
        }
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderRegistry.TAG_DROP_PROVIDER.get();
    }
}
