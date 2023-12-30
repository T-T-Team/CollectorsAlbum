package team.tnt.collectoralbum.common.item;

import api.tnt.collectoralbum.cards.CardGroup;
import api.tnt.collectoralbum.cards.CardProperties;
import api.tnt.collectoralbum.cards.IndexedCollectibleCard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;

public class NumberedCard extends Card implements IndexedCollectibleCard {

    private final int cardIndex;

    public NumberedCard(ResourceLocation identifier, CardGroup group, Map<Item, CardProperties> propertiesMap, int index) {
        super(identifier, group, propertiesMap);
        this.cardIndex = index;
    }

    @Override
    public int getCardIndex() {
        return cardIndex;
    }
}
