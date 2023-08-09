package team.tnt.collectoralbum.util;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.common.CardCategory;
import team.tnt.collectoralbum.common.CardDefinition;
import team.tnt.collectoralbum.common.ICardCategory;
import team.tnt.collectoralbum.common.container.AlbumContainer;
import team.tnt.collectoralbum.common.init.ItemRegistry;
import team.tnt.collectoralbum.common.item.AlbumItem;
import team.tnt.collectoralbum.common.item.CardRarity;
import team.tnt.collectoralbum.common.item.ICard;

public final class PlayerHelper {

    public static void giveItem(Player player, ItemStack itemStack) {
        if (!player.addItem(itemStack)) {
            ItemEntity entity = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), itemStack.copy());
            entity.setNoPickUpDelay();
            player.level.addFreshEntity(entity);
        }
        if (!CollectorsAlbum.config.autoEquipUnpackedCards)
            return;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            tryMoveCardIntoAlbum(player.getInventory().getItem(i), player);
        }
    }
    
    public static boolean tryMoveCardIntoAlbum(ItemStack stack, Player player) {
        if (!(stack.getItem() instanceof ICard card)) {
            return false;
        }
        ItemStack albumStack = findAlbum(player.getInventory());
        if (albumStack.isEmpty()) {
            return false;
        }
        AlbumContainer container = new AlbumContainer(albumStack);
        CardDefinition definition = card.getCard();
        ICardCategory cardCategory = definition.category();
        SimpleContainer simpleContainer = container.forCategory(cardCategory);
        int index = definition.cardNumber();
        ItemStack equippedItemStack = simpleContainer.getItem(index);
        if (equippedItemStack.isEmpty()) {
            ItemStack movedCard = stack.copy();
            movedCard.setCount(1);
            simpleContainer.setItem(index, movedCard);
            stack.shrink(1);
            return true;
        }
        if (!(equippedItemStack.getItem() instanceof ICard equippedCard)) {
            return false;
        }
        CardRarity equippedRarity = equippedCard.getCardRarity();
        CardRarity pickedUpRarity = card.getCardRarity();
        if (pickedUpRarity.getValue() > equippedRarity.getValue()) {
            ItemStack prevItemStack = equippedItemStack.copy();
            ItemStack moved = stack.copy();
            moved.setCount(1);
            simpleContainer.setItem(index, moved);
            stack.shrink(1);
            if (!player.level.isClientSide) {
                ItemEntity entity = new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), prevItemStack);
                entity.setNoPickUpDelay();
                player.level.addFreshEntity(entity);
            }
            return true;
        }
        return false;
    }
    
    public static ItemStack findAlbum(Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() == ItemRegistry.ALBUM.get()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private PlayerHelper() {
    }
}
