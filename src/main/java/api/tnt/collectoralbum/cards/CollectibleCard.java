package api.tnt.collectoralbum.cards;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Set;

public interface CollectibleCard {

    ResourceLocation getIdentifier();

    CardGroup getGroup();

    CardProperties getProperties(Item item);

    Set<Item> getItems();
}
