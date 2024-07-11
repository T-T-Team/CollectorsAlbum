package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.init.CardTypeRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;

import java.util.List;

public class SimpleCard implements AlbumCard {

    public static final MapCodec<SimpleCard> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(SimpleCard::identifier),
            ItemStack.SIMPLE_ITEM_CODEC.fieldOf("item").forGetter(SimpleCard::asItem),
            ResourceLocation.CODEC.fieldOf("category").forGetter(SimpleCard::category),
            CardUiTemplate.CODEC.optionalFieldOf("template", CardUiTemplate.TEMPLATE).forGetter(SimpleCard::template),
            Codec.INT.optionalFieldOf("points", 0).forGetter(SimpleCard::getPoints),
            ExtraCodecs.POSITIVE_INT.fieldOf("number").forGetter(SimpleCard::cardNumber)
    ).apply(instance, SimpleCard::new));

    private final ResourceLocation cardId;
    private final ItemStack item;
    private final ResourceLocation category;
    private final CardUiTemplate template;
    private final int points;
    private final int number;
    private AlbumCategory cachedCategory;

    public SimpleCard(ResourceLocation cardId, ItemStack item, ResourceLocation category, CardUiTemplate template, int points, int number) {
        this.cardId = cardId;
        this.item = item;
        this.category = category;
        this.template = template;
        this.points = points;
        this.number = number;
    }

    @Override
    public int cardNumber() {
        return number;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public ResourceLocation category() {
        return this.category;
    }

    @Override
    public CardUiTemplate template() {
        return this.template;
    }

    @Override
    public ItemStack asItem() {
        return this.item;
    }

    @Override
    public ResourceLocation identifier() {
        return this.cardId;
    }

    @Override
    public boolean test(CardFilter filter) {
        if (!filter.numberFilter().test(this.number)) {
            return false;
        }
        return filter.pointFilter().test(this.points);
    }

    @Override
    public void appendItemStackHoverTooltip(ItemStack itemStack, Item.TooltipContext context, List<Component> tooltips, TooltipFlag flag) {
        tooltips.add(CardUiTemplate.DEFAULT_CARD_INFO_HEADER);
        tooltips.add(CardUiTemplate.getCardNumberTooltip(this.number));
        tooltips.add(CardUiTemplate.getCardCategoryTooltip(this));
        int points = this.getPoints();
        if (points > 0) {
            tooltips.add(CardUiTemplate.getCardPointsTooltip(points));
        }
    }

    @Override
    public AlbumCardType<?> getType() {
        return CardTypeRegistry.CARD.get();
    }

    @Override
    public AlbumCategory getLinkedCategory() {
        if (this.cachedCategory == null) {
            this.cachedCategory = AlbumCategoryManager.getInstance().findById(this.category).orElseThrow();
        }
        return this.cachedCategory;
    }
}
