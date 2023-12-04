package team.tnt.collectoralbum.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.common.CardCategoryIndexPool;
import team.tnt.collectoralbum.common.CardDefinition;

import java.util.List;

public class CardItem extends Item implements ICard {

    private static final String TEXT_CARD_NUMBER = "card.tooltip.number";
    private static final String TEXT_CARD_RARITY = "card.tooltip.rarity";
    private static final String TEXT_CARD_CATEGORY = "card.tooltip.category";
    private static final String TEXT_CARD_VALUE = "card.tooltip.value";

    private final CardDefinition card;
    private final CardRarity rarity;
    private final int cardNumber;

    public CardItem(CardRarity rarity, CardDefinition card) {
        super(new Properties());
        this.card = card;
        this.rarity = rarity;
        this.cardNumber = CardCategoryIndexPool.getIndexOffset(card.category()) + card.cardNumber();
    }

    @Override
    public CardDefinition getCard() {
        return card;
    }

    @Override
    public CardRarity getCardRarity() {
        return rarity;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CardItem cardItem = (CardItem) stack.getItem();
        CardDefinition definition = cardItem.getCard();
        Component numberComponent = Component.literal("#" + cardNumber).withStyle(ChatFormatting.YELLOW);
        Component categoryComponent = Component.literal(definition.category().getTranslatedName().getString()).withStyle(definition.category().getTooltipFormat());
        Component rarityComponent = rarity.getTranslatedName();
        Component valueComponent = Component.literal(rarity.getValue() + " pts").withStyle(ChatFormatting.WHITE);
        tooltipComponents.add(Component.translatable(TEXT_CARD_NUMBER, numberComponent).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(TEXT_CARD_CATEGORY, categoryComponent).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(TEXT_CARD_RARITY, rarityComponent).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(TEXT_CARD_VALUE, valueComponent).withStyle(ChatFormatting.GRAY));
    }
}
