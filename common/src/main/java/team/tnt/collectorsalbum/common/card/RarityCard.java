package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.init.CardTypeRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.platform.Codecs;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class RarityCard implements RarityHolder {

    public static final MapCodec<RarityCard> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(RarityCard::identifier),
            Codecs.simpleEnumCodec(CardRarity.class, text -> text.toUpperCase(Locale.ROOT)).fieldOf("rarity").forGetter(t -> t.rarity),
            Codecs.SIMPLE_ITEM_CODEC.fieldOf("item").forGetter(RarityCard::asItem),
            ResourceLocation.CODEC.fieldOf("category").forGetter(RarityCard::category),
            ExtraCodecs.POSITIVE_INT.fieldOf("number").forGetter(RarityCard::cardNumber)
    ).apply(instance, RarityCard::new));

    private final ResourceLocation cardIdentifier;
    private final CardRarity rarity;
    private final ItemStack itemStack;
    private final ResourceLocation categoryIdentifier;
    private final int cardNumber;
    private final CardUiTemplate template;

    private AlbumCategory cachedCategory;

    public RarityCard(ResourceLocation cardIdentifier, CardRarity rarity, ItemStack itemStack, ResourceLocation categoryIdentifier, int cardNumber) {
        this.cardIdentifier = cardIdentifier;
        this.rarity = rarity;
        this.itemStack = itemStack;
        this.categoryIdentifier = categoryIdentifier;
        this.cardNumber = cardNumber;
        this.template = new CardUiTemplate(rarity.getColors(), rarity.getDurations(), rarity.getFlipSoundRef());
    }

    @Override
    public ResourceLocation identifier() {
        return this.cardIdentifier;
    }

    @Override
    public CardUiTemplate template() {
        return template;
    }

    @Override
    public CardRarity rarity() {
        return this.rarity;
    }

    @Override
    public ItemStack asItem() {
        return this.itemStack;
    }

    @Override
    public ResourceLocation category() {
        return this.categoryIdentifier;
    }

    @Override
    public int getPoints() {
        return this.rarity.getValue();
    }

    @Override
    public int cardNumber() {
        return this.cardNumber;
    }

    @Override
    public AlbumCardType<?> getType() {
        return CardTypeRegistry.RARITY_CARD.get();
    }

    @Override
    public boolean test(CardFilter filter) {
        Set<CardRarity> rarities = filter.rarities();
        if (rarities != null && !rarities.isEmpty() && !rarities.contains(rarity)) {
            return false;
        }
        if (!filter.numberFilter().test(this.cardNumber)) {
            return false;
        }
        return filter.pointFilter().test(this.rarity.getValue());
    }

    @Override
    public void appendItemStackHoverTooltip(ItemStack itemStack, List<Component> tooltips, TooltipFlag flag) {
        tooltips.add(CardUiTemplate.DEFAULT_CARD_INFO_HEADER);
        tooltips.add(CardUiTemplate.getCardNumberTooltip(this.cardNumber));
        tooltips.add(CardUiTemplate.getCardCategoryTooltip(this));
        tooltips.add(CardUiTemplate.getCardRarityTooltip(this.rarity));
        tooltips.add(CardUiTemplate.getCardPointsTooltip(this));
    }

    @Override
    public AlbumCategory getLinkedCategory() {
        if (this.cachedCategory == null) {
            this.cachedCategory = AlbumCategoryManager.getInstance().findById(this.categoryIdentifier).orElseThrow();
        }
        return this.cachedCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RarityCard that = (RarityCard) o;
        return Objects.equals(cardIdentifier, that.cardIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cardIdentifier);
    }
}
