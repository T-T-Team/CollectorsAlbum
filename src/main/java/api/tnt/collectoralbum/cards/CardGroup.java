package api.tnt.collectoralbum.cards;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public interface CardGroup {

    ResourceLocation getIdentifier();

    Set<CollectibleCard> getAvailableCards();

    Component getDisplayName();

    default int getSortingIndex() {
        return 99;
    }
}
