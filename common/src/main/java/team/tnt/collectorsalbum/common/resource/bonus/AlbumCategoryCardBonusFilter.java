package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumCard;
import team.tnt.collectorsalbum.common.card.CardRarity;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.function.ConstantNumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProviderType;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.platform.Codecs;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class AlbumCategoryCardBonusFilter implements IntermediateAlbumBonus {

    public static final MapCodec<AlbumCategoryCardBonusFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("category").forGetter(t -> t.category),
            Codecs.setCodec(Codecs.simpleEnumCodec(CardRarity.class, k -> k.toUpperCase(Locale.ROOT)), EnumSet::copyOf).optionalFieldOf("rarityFilter").forGetter(t -> t.rarityFilter),
            IntFilter.CODEC.optionalFieldOf("pointFilter", IntFilter.NO_FILTER).forGetter(t -> t.valueFilter),
            IntFilter.CODEC.optionalFieldOf("numberFilter", IntFilter.NO_FILTER).forGetter(t -> t.numberFilter),
            IntFilter.CODEC.fieldOf("cardCount").forGetter(t -> t.cardCount),
            AlbumBonusType.INSTANCE_CODEC.fieldOf("item").forGetter(t -> t.item)
    ).apply(instance, AlbumCategoryCardBonusFilter::new));

    private final ResourceLocation category;
    private final Optional<Set<CardRarity>> rarityFilter;
    private final IntFilter valueFilter;
    private final IntFilter numberFilter;
    private final IntFilter cardCount;
    private final AlbumBonus item;

    public AlbumCategoryCardBonusFilter(ResourceLocation category, Optional<Set<CardRarity>> rarityFilter, IntFilter valueFilter, IntFilter numberFilter, IntFilter cardCount, AlbumBonus item) {
        this.category = category;
        this.rarityFilter = rarityFilter;
        this.valueFilter = valueFilter;
        this.numberFilter = numberFilter;
        this.cardCount = cardCount;
        this.item = item;
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
        return context.get(ActionContext.ALBUM, Album.class).map(album -> {
            Collection<AlbumCard> cards = album.getCardsForCategory(this.category);
            List<AlbumCard> validCards = cards.stream().filter(card -> {
                CardRarity rarity = card.rarity();
                Set<CardRarity> rarityFilter = this.rarityFilter.orElse(Collections.emptySet());
                if (!rarityFilter.contains(rarity)) {
                    return false;
                }
                int value = card.getPoints();
                if (!valueFilter.test(value)) {
                    return false;
                }
                int number = card.cardNumber();
                return numberFilter.test(number);
            }).toList();
            return this.cardCount.test(validCards.size());
        }).orElse(false);
    }

    public record IntFilter(NumberProvider min, NumberProvider max) {

        public static final MapCodec<IntFilter> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.either(Codec.INT, NumberProviderType.INSTANCE_CODEC).optionalFieldOf("min", Either.left(Integer.MIN_VALUE)).forGetter(v -> Either.right(v.min)),
                Codec.either(Codec.INT, NumberProviderType.INSTANCE_CODEC).optionalFieldOf("max", Either.left(Integer.MAX_VALUE)).forGetter(v -> Either.right(v.max))
        ).apply(instance, IntFilter::new));
        public static final Codec<IntFilter> CODEC = MAP_CODEC.codec();
        public static final IntFilter NO_FILTER = new IntFilter(new ConstantNumberProvider(Integer.MIN_VALUE), new ConstantNumberProvider(Integer.MAX_VALUE));

        private IntFilter(Either<Integer, NumberProvider> min, Either<Integer, NumberProvider> max) {
            this(min.map(ConstantNumberProvider::new, Function.identity()), max.map(ConstantNumberProvider::new, Function.identity()));
        }

        public boolean test(int value) {
            return value >= min.intValue() && value <= max.intValue();
        }
    }
}
