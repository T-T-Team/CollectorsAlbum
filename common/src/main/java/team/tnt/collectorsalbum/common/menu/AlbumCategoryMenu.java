package team.tnt.collectorsalbum.common.menu;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.AlbumCategoryUiTemplate;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.init.MenuRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;

import java.util.List;

public class AlbumCategoryMenu extends AbstractContainerMenu {

    private ResourceLocation category;
    private final Int2IntMap cardNumberToSlotCache = new Int2IntOpenHashMap();

    public AlbumCategoryMenu(int menuId, Inventory inventory) {
        super(MenuRegistry.ALBUM_CATEGORY.get(), menuId);
    }

    public AlbumCategoryMenu(int menuId, Inventory playerInventory, ResourceLocation category) {
        this(menuId, playerInventory);
        this.category = category;

        ItemStack itemStack = playerInventory.player.getMainHandItem();
        Album album = itemStack.get(ItemDataComponentRegistry.ALBUM.get());
        if (album == null) {
            return;
        }

        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        AlbumCategory albumCategory = manager.findById(category).orElse(null);
        if (albumCategory != null) {
            AlbumCategoryUiTemplate template = albumCategory.visualTemplate();
            AlbumCategoryUiTemplate.SlotPositionTemplate slotTemplate = template.slotTemplate;
            int xStart = slotTemplate.xAlbumSlotPositionStart();
            int yStart = slotTemplate.yAlbumSlotPositionStart();
            int xSpacing = slotTemplate.xSlotSpacing();
            int ySpacing = slotTemplate.ySlotSpacing();
            int pageWidth = slotTemplate.pageWidth();
            int[] cardNumbers = albumCategory.getCardNumbers();
            int slotsPerPage = slotTemplate.columns() * slotTemplate.rows();
            Container wrapper = new AlbumInventoryWrapper(cardNumbers.length, itemStack, category, album);
            // left page cards
            for (int y = 0; y < slotTemplate.rows(); y++) {
                for (int x = 0; x < slotTemplate.columns(); x++) {
                    int cardX = xStart + x * xSpacing;
                    int cardY = yStart + y * ySpacing;
                    int cardIndex = x + (y * slotTemplate.columns());
                    if (cardIndex >= cardNumbers.length)
                        break;
                    addSlot(new CardSlot(wrapper, cardIndex, cardX, cardY, category, cardNumbers[cardIndex]));
                    this.cardNumberToSlotCache.put(cardNumbers[cardIndex], cardIndex);
                }
            }
            // right page cards
            if (cardNumbers.length >= slotsPerPage) {
                int cardsLength = slotTemplate.columns() * xSpacing - xSpacing;
                int rightStartX = pageWidth * 2 - 18 - cardsLength - xStart + 2;
                for (int y = 0; y < slotTemplate.rows(); y++) {
                    for (int x = 0; x < slotTemplate.columns(); x++) {
                        int cardX = rightStartX + x * xSpacing;
                        int cardY = yStart + y * ySpacing;
                        int cardIndex = slotsPerPage + x + (y * slotTemplate.columns());
                        if (cardIndex >= cardNumbers.length)
                            break;
                        addSlot(new CardSlot(wrapper, cardIndex, cardX, cardY, category, cardNumbers[cardIndex]));
                        this.cardNumberToSlotCache.put(cardNumbers[cardIndex], cardIndex);
                    }
                }
            }

            int playerX = slotTemplate.xPlayerSlotPositionStart();
            int playerY = slotTemplate.yPlayerSlotPositionStart();
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 9; x++) {
                    addSlot(new AlbumRestrictedSlot(playerInventory, 9 + x + (y * 9), playerX + 1 + x * 18, playerY + 1 + y * 18));
                }
            }
            for (int x = 0; x < 9; x++) {
                addSlot(new AlbumRestrictedSlot(playerInventory, x, playerX + 1 + x * 18, playerY + 59));
            }
        }
    }

    @Override
    public void removed(Player player) {
        CollectorsAlbum.forceAlbumReload(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            itemStack = slotItem.copy();

            AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
            int slotsCount = manager.findById(category).map(cat -> cat.getCardNumbers().length).orElse(0);
            if (slotsCount == 0) {
                return this.transferInInventory(slotItem, index, 0);
            }

            if (index >= 0 && index < slotsCount) {
                // Extraction
                slot.setChanged();
                if (!moveItemStackTo(slotItem, slotsCount, slotsCount + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= slotsCount && index < slotsCount + 36) {
                // Insert to album
                AlbumCard albumCard = this.getCardInfo(slotItem);
                if (albumCard == null || !albumCard.category().equals(this.category)) {
                    return this.transferInInventory(slotItem, index, slotsCount);
                }

                int cardNumber = albumCard.cardNumber();
                if (!this.cardNumberToSlotCache.containsKey(cardNumber)) {
                    return this.transferInInventory(slotItem, index, slotsCount);
                }
                int cardSlot = this.cardNumberToSlotCache.get(cardNumber);
                Slot targetSlot = this.slots.get(cardSlot);
                if (slotItem.getCount() > 1) {
                    if (!targetSlot.hasItem()) {
                        itemStack = slotItem.copy();
                        itemStack.setCount(1);
                        targetSlot.set(itemStack);
                        slotItem.shrink(1);
                        return slotItem;
                    }
                    return ItemStack.EMPTY;
                }

                AlbumCard replacement = null;
                if (targetSlot.hasItem()) {
                    ItemStack inSlot = targetSlot.getItem();
                    replacement = this.getCardInfo(inSlot);
                }

                if (replacement == null || albumCard.compareTo(replacement) > 0) {
                    itemStack = targetSlot.getItem().copy();
                    targetSlot.set(slotItem);
                    slot.set(itemStack.copy());
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public AlbumCategory getCategory() {
        if (category == null) {
            throw new IllegalStateException("Category is not defined");
        }
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        return manager.findById(category).orElseThrow();
    }

    private ItemStack transferInInventory(ItemStack itemStack, int index, int cards) {
        if (index >= cards && index < cards + 27) {
            if (!moveItemStackTo(itemStack, cards + 27, cards + 36, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= cards + 27 && index < cards + 36) {
            if (!moveItemStackTo(itemStack, cards, cards + 9, false)) {
                return ItemStack.EMPTY;
            }
        }
        return itemStack;
    }

    private AlbumCard getCardInfo(ItemStack itemStack) {
        return AlbumCardManager.getInstance().getCardInfo(itemStack.getItem()).orElse(null);
    }

    private static final class CardSlot extends Slot {

        private final ResourceLocation category;
        private final int cardNumber;

        public CardSlot(Container container, int index, int slotX, int slotY, ResourceLocation category, int number) {
            super(container, index, slotX, slotY);
            this.category = category;
            this.cardNumber = number;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            AlbumCardManager manager = AlbumCardManager.getInstance();
            return manager.getCardInfo(itemStack.getItem()).map(info -> {
                ResourceLocation cardCategory = info.category();
                int cardNumber = info.cardNumber();
                return cardNumber == this.cardNumber && cardCategory.equals(category);
            }).orElse(false);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int getMaxStackSize(ItemStack $$0) {
            return getMaxStackSize();
        }
    }

    private static final class AlbumInventoryWrapper extends SimpleContainer {

        private final ItemStack itemStack;
        private final ResourceLocation category;

        AlbumInventoryWrapper(int size, ItemStack itemStack, ResourceLocation category, Album album) {
            super(size);
            this.itemStack = itemStack;
            this.category = category;

            List<ItemStack> itemStacks = album.getInventory(category);
            for (int i = 0; i < itemStacks.size(); i++) {
                ItemStack stack = itemStacks.get(i);
                if (!stack.isEmpty())
                    this.getItems().set(i, stack.copy());
            }
        }

        @Override
        public void setItem(int index, ItemStack itemStack) {
            Album album = this.itemStack.get(ItemDataComponentRegistry.ALBUM.get());
            if (album != null) {
                Album.Mutable mutable = new Album.Mutable(album);
                mutable.set(this.category, index, itemStack.copy());
                Album updated = mutable.toImmutable();
                this.itemStack.set(ItemDataComponentRegistry.ALBUM.get(), updated);
            }
            super.setItem(index, itemStack);
        }
    }
}
