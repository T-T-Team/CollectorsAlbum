package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.AlbumCategoryUiTemplate;
import team.tnt.collectorsalbum.common.init.CardTypeRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.platform.Codecs;

import java.util.*;

public class RarityCard implements RarityHolder {

    public static final MapCodec<RarityCard> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(AlbumCard::enabled),
            Identifier.CODEC.fieldOf("id").forGetter(RarityCard::identifier),
            Codecs.simpleEnumCodec(CardRarity.class, text -> text.toUpperCase(Locale.ROOT)).fieldOf("rarity").forGetter(t -> t.rarity),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(t -> t.item),
            Identifier.CODEC.fieldOf("category").forGetter(RarityCard::category),
            ExtraCodecs.POSITIVE_INT.fieldOf("number").forGetter(RarityCard::cardNumber),
            Identifier.CODEC.optionalFieldOf("cardTexture").forGetter(t -> Optional.ofNullable(t.template.cardTexture()))
    ).apply(instance, RarityCard::new));

    private final boolean enabled;
    private final Identifier cardIdentifier;
    private final CardRarity rarity;
    private final Item item;
    private final Identifier categoryIdentifier;
    private final int cardNumber;
    private final CardUiTemplate template;

    private AlbumCategory cachedCategory;

    public RarityCard(boolean enabled, Identifier cardIdentifier, CardRarity rarity, Item item, Identifier categoryIdentifier, int cardNumber, Optional<Identifier> cardTexture) {
        this.enabled = enabled;
        this.cardIdentifier = cardIdentifier;
        this.rarity = rarity;
        this.item = item;
        this.categoryIdentifier = categoryIdentifier;
        this.cardNumber = cardNumber;
        this.template = new CardUiTemplate(rarity.getColors(), rarity.getDurations(), rarity.getFlipSoundRef(), cardTexture.orElse(null));
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public Identifier identifier() {
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
    public Item asItem() {
        return this.item;
    }

    @Override
    public Identifier category() {
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
    public void appendItemStackHoverTooltip(ItemStack itemStack, Item.TooltipContext context, List<Component> tooltips, TooltipFlag flag) {
        tooltips.add(CardUiTemplate.DEFAULT_CARD_INFO_HEADER);
        String prefix = this.cachedCategory != null ? this.cachedCategory.visualTemplate().cardNumberPrefix : AlbumCategoryUiTemplate.DEFAULT_TEMPLATE.cardNumberPrefix;
        tooltips.add(CardUiTemplate.getCardNumberTooltip(prefix, this.cardNumber));
        tooltips.add(CardUiTemplate.getCardCategoryTooltip(this));
        tooltips.add(CardUiTemplate.getCardRarityTooltip(this.rarity));
        tooltips.add(CardUiTemplate.getCardPointsTooltip(this));
    }

    @Override
    public AlbumCategory getLinkedCategory() {
        if (this.cachedCategory == null) {
            this.cachedCategory = AlbumCategoryManager.getInstance().findById(this.categoryIdentifier).orElse(null);
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
