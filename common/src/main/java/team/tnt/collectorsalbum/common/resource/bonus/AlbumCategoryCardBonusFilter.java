package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumBonusDescriptionOutput;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.CommonLabels;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.card.CardCategoryFilter;
import team.tnt.collectorsalbum.common.card.CardUiTemplate;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AlbumCategoryCardBonusFilter implements IntermediateAlbumBonus {

    public static final Component UNKNOWN_CATEGORY_LABEL = Component.translatable("collectorsalbum.label.unknown").withStyle(ChatFormatting.RED);
    public static final String MATCHED = "collectorsalbum.label.bonus.matched_cards";

    public static final MapCodec<AlbumCategoryCardBonusFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("category").forGetter(t -> t.category),
            CardCategoryFilter.CODEC.optionalFieldOf("filter", CardCategoryFilter.NO_FILTER).forGetter(t -> t.filter),
            AlbumBonusType.INSTANCE_CODEC.fieldOf("item").forGetter(t -> t.item)
    ).apply(instance, AlbumCategoryCardBonusFilter::new));

    private final ResourceLocation category;
    private final CardCategoryFilter filter;
    private final AlbumBonus item;

    public AlbumCategoryCardBonusFilter(ResourceLocation category, CardCategoryFilter filter, AlbumBonus item) {
        this.category = category;
        this.filter = filter;
        this.item = item;
    }

    @Override
    public void addDescription(AlbumBonusDescriptionOutput description) {
        Component categoryDisplayLabel = AlbumCategoryManager.getInstance().findById(this.category)
                .map(AlbumCategory::getDisplayText).orElse(UNKNOWN_CATEGORY_LABEL);
        Component categoryIdentifierLabel = Component.literal(this.category.toString());
        description.text(Component.translatable(CardUiTemplate.ITEM_TOOLTIP_CATEGORY_KEY, categoryDisplayLabel), categoryIdentifierLabel);
        description.nested(() -> {
            if (this.filter != CardCategoryFilter.NO_FILTER) {
                description.text(CardCategoryFilter.LABEL_FILTER);
                description.nested(() -> this.filter.generateDescriptionLabels(description));
            }
            boolean canApply = this.canApply(description.getContext());
            Component matchingCards = Component.literal(String.valueOf(this.getMatchingCards(description.getContext())))
                    .withStyle(AlbumBonusDescriptionOutput.getBooleanColor(canApply));
            Component matched = Component.translatable(MATCHED, matchingCards);
            description.text(matched, this.filter.cardCountFilter().getDisplayComponent());
            description.condition(CommonLabels.APPLIES, CommonLabels.getBoolState(canApply), canApply, this);
        });
    }

    @Override
    public void apply(ActionContext context) {
        if (this.canApply(context)) {
            this.item.apply(context);
        } else {
            this.item.removed(context);
        }
    }

    @Override
    public void removed(ActionContext context) {
        this.item.removed(context);
    }

    @Override
    public List<AlbumBonus> children() {
        return Collections.singletonList(this.item);
    }

    @Override
    public AlbumBonusType<?> getType() {
        return AlbumBonusRegistry.CATEGORY_FILTER.get();
    }

    @Override
    public boolean canApply(ActionContext context) {
        int matching = this.getMatchingCards(context);
        return this.filter.cardCountFilter().test(matching);
    }

    private int getMatchingCards(ActionContext context) {
        return context.get(ActionContext.ALBUM, Album.class).map(album -> {
            Collection<AlbumCard> cards = album.getCardsForCategory(this.category);
            List<AlbumCard> validCards = cards.stream().filter(card -> card.test(this.filter)).toList();
            return validCards.size();
        }).orElse(0);
    }
}
