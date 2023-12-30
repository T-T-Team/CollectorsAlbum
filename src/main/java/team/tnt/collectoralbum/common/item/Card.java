package team.tnt.collectoralbum.common.item;

import api.tnt.collectoralbum.cards.CardGroup;
import api.tnt.collectoralbum.cards.CardProperties;
import api.tnt.collectoralbum.cards.CollectibleCard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.Set;

public class Card implements CollectibleCard {

    private final ResourceLocation identifier;
    private final CardGroup group;
    private final Map<Item, CardProperties> propertiesMap;

    public Card(ResourceLocation identifier, CardGroup group, Map<Item, CardProperties> propertiesMap) {
        this.identifier = identifier;
        this.group = group;
        this.propertiesMap = propertiesMap;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    @Override
    public CardGroup getGroup() {
        return group;
    }

    @Override
    public CardProperties getProperties(Item item) {
        return propertiesMap.get(item);
    }

    @Override
    public Set<Item> getItems() {
        return propertiesMap.keySet();
    }
}
