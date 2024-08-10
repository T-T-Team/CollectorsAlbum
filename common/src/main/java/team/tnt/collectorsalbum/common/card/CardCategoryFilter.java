package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import team.tnt.collectorsalbum.common.AlbumBonusDescriptionOutput;
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
    public static final Component LABEL_FILTER = Component.translatable("collectorsalbum.label.filter");
    public static final Component LABEL_FILTER_RARITY = Component.translatable("collectorsalbum.label.filter.rarities");
    public static final Component LABEL_FILTER_NUMBER = Component.translatable("collectorsalbum.label.filter.numbers");
    public static final Component LABEL_FILTER_POINT = Component.translatable("collectorsalbum.label.filter.points");
    public static final Component LABEL_FILTER_CARDS = Component.translatable("collectorsalbum.label.filter.cards");

    public void generateDescriptionLabels(AlbumBonusDescriptionOutput descriptionOutput) {
        if (!rarities.isEmpty()) {
            Component raritiesTooltip = Component.literal("[" + String.join(",", this.rarities().stream().map(rarity -> rarity.getDisplayText().getString()).toList()) + "]");
            descriptionOutput.text(LABEL_FILTER_RARITY, raritiesTooltip);
        }
        if (numberFilter != IntFilter.NO_FILTER) {
            descriptionOutput.text(LABEL_FILTER_NUMBER, this.numberFilter.getDisplayComponent());
        }
        if (pointFilter != IntFilter.NO_FILTER) {
            descriptionOutput.text(LABEL_FILTER_POINT, this.pointFilter.getDisplayComponent());
        }
        if (cardCountFilter != IntFilter.NO_FILTER) {
            descriptionOutput.text(LABEL_FILTER_CARDS, this.cardCountFilter.getDisplayComponent());
        }
    }
}
