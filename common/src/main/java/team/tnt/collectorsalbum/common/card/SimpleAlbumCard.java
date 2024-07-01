package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.AlbumCard;
import team.tnt.collectorsalbum.common.AlbumCardType;
import team.tnt.collectorsalbum.common.AlbumCategoryType;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.init.CardTypeRegistry;

public record SimpleAlbumCard(ResourceLocation identifier, ItemStack itemStack, AlbumCategoryType<?> category, int cardNumber) implements AlbumCard {

    public static final MapCodec<SimpleAlbumCard> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("card_id").forGetter(SimpleAlbumCard::identifier),
            ItemStack.SIMPLE_ITEM_CODEC.fieldOf("item").forGetter(SimpleAlbumCard::itemStack),
            CollectorsAlbumRegistries.CATEGORY.codec().fieldOf("category").forGetter(SimpleAlbumCard::category),
            ExtraCodecs.POSITIVE_INT.fieldOf("number").forGetter(SimpleAlbumCard::cardNumber)
    ).apply(instance, SimpleAlbumCard::new));

    @Override
    public ItemStack asItem() {
        return this.itemStack;
    }

    @Override
    public AlbumCardType<?> getType() {
        return CardTypeRegistry.CARD.get();
    }
}
