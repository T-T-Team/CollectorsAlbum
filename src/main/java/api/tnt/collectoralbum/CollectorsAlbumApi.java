package api.tnt.collectoralbum;

import api.tnt.collectoralbum.cards.CardGroup;
import api.tnt.collectoralbum.cards.CollectibleCard;
import api.tnt.collectoralbum.data.CardGroupRegistry;
import api.tnt.collectoralbum.data.CollectibleCardRegistry;
import api.tnt.collectoralbum.data.SimpleDataRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class CollectorsAlbumApi {

    public static final Logger LOGGER = LogManager.getLogger("CollectorsAlbum");
    private static final SimpleDataRegistry<Item, CollectibleCard> CARD_REGISTRY = new CollectibleCardRegistry();
    private static final SimpleDataRegistry<ResourceLocation, CardGroup> GROUP_REGISTRY = new CardGroupRegistry();

    public static SimpleDataRegistry<Item, CollectibleCard> getCardsRegistry() {
        return CARD_REGISTRY;
    }

    public static SimpleDataRegistry<ResourceLocation, CardGroup> getGroupsRegistry() {
        return GROUP_REGISTRY;
    }

    public static Optional<CollectibleCard> getCardProperties(Item item) {
        if (item instanceof CollectibleCard collectibleCard) {
            return Optional.of(collectibleCard);
        }
        return CARD_REGISTRY.getValue(item);
    }
}
