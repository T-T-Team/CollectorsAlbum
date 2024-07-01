package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import team.tnt.collectorsalbum.common.AlbumCard;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.AlbumCategoryType;
import team.tnt.collectorsalbum.common.init.CategoryRegistry;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AlbumCategoryImpl implements AlbumCategory {

    public static final MapCodec<AlbumCategoryImpl> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("maxSize").forGetter(t -> t.maxSize),
            ResourceLocation.CODEC.fieldOf("identifier").forGetter(AlbumCategoryImpl::identifier)
    ).apply(instance, AlbumCategoryImpl::new));

    private final int maxSize;
    private final ResourceLocation identifier;
    private final Component displayText;
    private final AlbumCard[] cards;
    private final Set<ResourceLocation> acceptedCards = new HashSet<>();

    public AlbumCategoryImpl(int maxSize, ResourceLocation identifier) {
        this.maxSize = maxSize;
        this.identifier = identifier;
        this.displayText = Component.translatable(null);
        this.cards = new AlbumCard[maxSize];
    }

    @Override
    public ResourceLocation identifier() {
        return this.identifier;
    }

    @Override
    public Component getDisplayText() {
        return this.displayText;
    }

    @Override
    public boolean accepts(AlbumCard card) {
        return this.acceptedCards.contains(card.identifier());
    }

    @Override
    public Optional<AlbumCard> getCardAt(int index) {
        return index >= 0 && index < cards.length ? Optional.ofNullable(cards[index]) : Optional.empty();
    }

    @Override
    public AlbumCategoryType<?> getType() {
        return CategoryRegistry.CATEGORY.get();
    }
}
