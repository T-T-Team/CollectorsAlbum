package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.card.CardCategoryFilter;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AlbumCategoryCardBonusFilter implements IntermediateAlbumBonus {

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
            List<AlbumCard> validCards = cards.stream().filter(card -> card.test(this.filter)).toList();
            return this.filter.cardCountFilter().test(validCards.size());
        }).orElse(false);
    }
}
