package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import team.tnt.collectorsalbum.platform.Codecs;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

public record CardCategoryFilter(Set<CardRarity> rarities, IntFilter numberFilter, IntFilter pointFilter, IntFilter cardCountFilter, IntFilter categoryPointFilter) implements CardFilter {

    public static final Codec<CardCategoryFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.setCodec(Codecs.simpleEnumCodec(CardRarity.class, s -> s.toUpperCase(Locale.ROOT))).optionalFieldOf("rarities", Collections.emptySet()).forGetter(CardCategoryFilter::rarities),
            IntFilter.CODEC.optionalFieldOf("numbers", IntFilter.NO_FILTER).forGetter(CardCategoryFilter::numberFilter),
            IntFilter.CODEC.optionalFieldOf("points", IntFilter.NO_FILTER).forGetter(CardCategoryFilter::pointFilter),
            IntFilter.CODEC.optionalFieldOf("cards", IntFilter.NO_FILTER).forGetter(CardCategoryFilter::cardCountFilter),
            IntFilter.CODEC.optionalFieldOf("categoryPoints", IntFilter.NO_FILTER).forGetter(CardCategoryFilter::categoryPointFilter)
    ).apply(instance, CardCategoryFilter::new));
    public static final CardCategoryFilter NO_FILTER = new CardCategoryFilter(Collections.emptySet(), IntFilter.NO_FILTER, IntFilter.NO_FILTER, IntFilter.NO_FILTER, IntFilter.NO_FILTER);

    public CardCategoryFilter filterRarities() {
        return new CardCategoryFilter(this.rarities, IntFilter.NO_FILTER, IntFilter.NO_FILTER, IntFilter.NO_FILTER, IntFilter.NO_FILTER);
    }

    public CardCategoryFilter filterNumbers() {
        return new CardCategoryFilter(Collections.emptySet(), this.numberFilter, IntFilter.NO_FILTER, IntFilter.NO_FILTER, IntFilter.NO_FILTER);
    }

    public CardCategoryFilter filterPoints() {
        return new CardCategoryFilter(Collections.emptySet(), IntFilter.NO_FILTER, this.pointFilter, IntFilter.NO_FILTER, IntFilter.NO_FILTER);
    }

    public CardCategoryFilter filterCards() {
        return new CardCategoryFilter(Collections.emptySet(), IntFilter.NO_FILTER, IntFilter.NO_FILTER, this.cardCountFilter, IntFilter.NO_FILTER);
    }

    public CardCategoryFilter filterCategoryPoints() {
        return new CardCategoryFilter(Collections.emptySet(), IntFilter.NO_FILTER, IntFilter.NO_FILTER, IntFilter.NO_FILTER, this.categoryPointFilter);
    }
}
