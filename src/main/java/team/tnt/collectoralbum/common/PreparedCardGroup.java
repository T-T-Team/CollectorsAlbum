package team.tnt.collectoralbum.common;

import api.tnt.collectoralbum.cards.CardGroup;
import api.tnt.collectoralbum.cards.CollectibleCard;
import api.tnt.collectoralbum.data.CollectibleCardRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Function;

public class PreparedCardGroup implements CardGroup {

    public static final Function<ResourceLocation, Codec<PreparedCardGroup>> CODEC_PROVIDER = identifier -> RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("cards").forGetter(PreparedCardGroup::getAvailableCardIds),
            Style.FORMATTING_CODEC.optionalFieldOf("displayStyle", Style.EMPTY).forGetter(t -> null),
            Codec.INT.optionalFieldOf("sortIndex", 99).forGetter(t -> t.sortIndex)
    ).apply(instance, (collectibleCards, style, index) -> new PreparedCardGroup(identifier, collectibleCards, style, index)));

    private final ResourceLocation identifier;
    private final List<ResourceLocation> availableCards;
    private final Component displayText;
    private final int sortIndex;

    private PreparedCardGroup(ResourceLocation identifier, List<ResourceLocation> availableCards, Style displayStyle, int sortIndex) {
        this.identifier = identifier;
        this.availableCards = availableCards;
        MutableComponent component = Component.translatable("card.group." + identifier.toString().replaceAll("[:/]", "."));
        component.setStyle(displayStyle);
        this.displayText = component;
        this.sortIndex = sortIndex;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    @Override
    public Set<CollectibleCard> getAvailableCards() {
        return Collections.emptySet();
    }

    @Override
    public Component getDisplayName() {
        return displayText;
    }

    public List<ResourceLocation> getAvailableCardIds() {
        return availableCards;
    }

    public PopulatedCardGroup populate(CollectibleCardRegistry registry) {
        Set<CollectibleCard> cards = availableCards.stream()
                .map(id -> registry.getValueById(id).orElseThrow(() -> new IllegalStateException("Unknown card: " + id)))
                .collect(LinkedHashSet::new, LinkedHashSet::add, AbstractCollection::addAll);
        List<String> invalidGroups = cards.stream().filter(t -> !t.getGroup().getIdentifier().equals(identifier))
                .map(t -> t.getIdentifier() + " -> " + t.getGroup().getIdentifier())
                .toList();
        if (invalidGroups.size() > 0) {
            throw new IllegalStateException("There are cards which are defined for different category: " + invalidGroups);
        }
        return new PopulatedCardGroup(identifier, cards, displayText, sortIndex);
    }
}
