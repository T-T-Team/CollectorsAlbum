package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import team.tnt.collectorsalbum.platform.Codecs;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

public record CardCategoryFilter(Set<CardRarity> rarities, IntFilter numberFilter, IntFilter pointFilter, IntFilter cardCountFilter) implements CardFilter {

    public static final Codec<CardCategoryFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.setCodec(Codecs.simpleEnumCodec(CardRarity.class, s -> s.toUpperCase(Locale.ROOT))).optionalFieldOf("rarities", Collections.emptySet()).forGetter(CardCategoryFilter::rarities),
            IntFilter.CODEC.optionalFieldOf("numbers", IntFilter.NO_FILTER).forGetter(CardCategoryFilter::numberFilter),
            IntFilter.CODEC.optionalFieldOf("points", IntFilter.NO_FILTER).forGetter(CardCategoryFilter::pointFilter),
            IntFilter.CODEC.optionalFieldOf("cards", IntFilter.NO_FILTER).forGetter(CardCategoryFilter::cardCountFilter)
    ).apply(instance, CardCategoryFilter::new));
    public static final CardCategoryFilter NO_FILTER = new CardCategoryFilter(Collections.emptySet(), IntFilter.NO_FILTER, IntFilter.NO_FILTER, IntFilter.NO_FILTER);
}
