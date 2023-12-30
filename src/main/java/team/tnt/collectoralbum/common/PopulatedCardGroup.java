package team.tnt.collectoralbum.common;

import api.tnt.collectoralbum.cards.CardGroup;
import api.tnt.collectoralbum.cards.CollectibleCard;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class PopulatedCardGroup implements CardGroup {

    private final ResourceLocation identifier;
    private final Set<CollectibleCard> availableCards;
    private final Component displayText;
    private final int sortIndex;

    PopulatedCardGroup(ResourceLocation identifier, Set<CollectibleCard> availableCards, Component displayText, int sortIndex) {
        this.identifier = identifier;
        this.availableCards = availableCards;
        this.displayText = displayText;
        this.sortIndex = sortIndex;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    @Override
    public Set<CollectibleCard> getAvailableCards() {
        return availableCards;
    }

    @Override
    public Component getDisplayName() {
        return displayText;
    }

    @Override
    public int getSortingIndex() {
        return sortIndex;
    }
}
