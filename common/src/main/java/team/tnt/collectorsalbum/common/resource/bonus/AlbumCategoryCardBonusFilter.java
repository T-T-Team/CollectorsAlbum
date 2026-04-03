package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.card.CardCategoryFilter;
import team.tnt.collectorsalbum.common.card.CardRarity;
import team.tnt.collectorsalbum.common.card.IntFilter;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public record AlbumCategoryCardBonusFilter(Identifier category, CardCategoryFilter filter, AlbumBonus item) implements IntermediateAlbumBonus {

    public static final Component UNKNOWN_CATEGORY_LABEL = Component.translatable("collectorsalbum.label.unknown").withStyle(ChatFormatting.RED);

    public static final MapCodec<AlbumCategoryCardBonusFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("category").forGetter(t -> t.category),
            CardCategoryFilter.CODEC.optionalFieldOf("filter", CardCategoryFilter.NO_FILTER).forGetter(t -> t.filter),
            AlbumBonusType.INSTANCE_CODEC.fieldOf("item").forGetter(t -> t.item)
    ).apply(instance, AlbumCategoryCardBonusFilter::new));

    @Override
    public void appendDetails(SectionOutput writer, ActionContext ctx) {
        if (this.filter == CardCategoryFilter.NO_FILTER)
            return;
        Album album = ctx.get(ActionContext.ALBUM, Album.class).orElse(null);
        if (album == null)
            return;
        Collection<AlbumCard> cards = album.getCardsForCategory(this.category);
        int matching = (int) cards.stream().filter(card -> card.test(this.filter)).count();
        Component categoryDisplayLabel = AlbumCategoryManager.getInstance().findById(this.category)
                .map(category -> (Component) category.getDisplayText().copy().withStyle(ChatFormatting.RESET))
                .orElse(UNKNOWN_CATEGORY_LABEL);

        boolean allFulfilled = this.canApply(ctx);
        writer.condition(allFulfilled, Component.translatable("collectorsalbum.label.bonus.category_filter", categoryDisplayLabel, matching));

        // matching rarities
        Set<CardRarity> requiredRarities = this.filter.rarities();
        if (!requiredRarities.isEmpty()) {
            int count = this.getMatchingCards(cards, this.filter.filterRarities());
            String rarities = String.join(",", requiredRarities.stream().map(rarity -> rarity.getDisplayText().getString()).toList());
            Component text = Component.literal("   ").append(Component.translatable("collectorsalbum.label.bonus.category_filter.rarities", rarities, count));
            writer.condition(allFulfilled, text);
        }

        // card numbers
        IntFilter numberFilter = this.filter.numberFilter();
        if (numberFilter != IntFilter.NO_FILTER) {
            int count = this.getMatchingCards(cards, this.filter.filterNumbers());
            Component range = numberFilter.getDisplayComponent();
            writer.condition(allFulfilled, Component.literal("   ").append(Component.translatable("collectorsalbum.label.bonus.category_filter.card_numbers", range, count)));
        }

        // card value
        IntFilter pointFilter = this.filter.pointFilter();
        if (pointFilter != IntFilter.NO_FILTER) {
            int count = this.getMatchingCards(cards, this.filter.filterPoints());
            Component range = pointFilter.getDisplayComponent();
            writer.condition(allFulfilled, Component.literal("   ").append(Component.translatable("collectorsalbum.label.bonus.category_filter.card_points", range, count)));
        }

        // collected matching cards
        IntFilter collectedFilter = this.filter.cardCountFilter();
        if (collectedFilter != IntFilter.NO_FILTER) {
            Component range = collectedFilter.getDisplayComponent();
            writer.condition(allFulfilled, Component.literal("   ").append(Component.translatable("collectorsalbum.label.bonus.category_filter.collected_cards", range, cards.size())));
        }

        // category points
        IntFilter pointsFilter = this.filter.categoryPointFilter();
        if (pointsFilter != IntFilter.NO_FILTER) {
            int categoryValue = cards.stream().filter(card -> card.test(this.filter)).mapToInt(AlbumCard::getPoints).sum();
            Component range = pointsFilter.getDisplayComponent();
            writer.condition(allFulfilled, Component.literal("   ").append(Component.translatable("collectorsalbum.label.bonus.category_filter.category_points", range, categoryValue)));
        }
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
        List<AlbumCard> matching = this.getMatchingCards(context);
        int categoryValue = matching.stream().mapToInt(AlbumCard::getPoints).sum();
        return this.filter.categoryPointFilter().test(categoryValue) && this.filter.cardCountFilter().test(matching.size());
    }

    private List<AlbumCard> getMatchingCards(ActionContext context) {
        return context.get(ActionContext.ALBUM, Album.class).map(album -> {
            Collection<AlbumCard> cards = album.getCardsForCategory(this.category);
            return cards.stream().filter(card -> card.test(this.filter)).toList();
        }).orElse(Collections.emptyList());
    }

    private int getMatchingCards(Collection<AlbumCard> cards, CardCategoryFilter filter) {
        return (int) cards.stream().filter(card -> card.test(filter)).count();
    }
}
