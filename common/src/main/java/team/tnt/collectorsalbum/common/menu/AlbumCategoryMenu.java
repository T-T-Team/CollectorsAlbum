package team.tnt.collectorsalbum.common.menu;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.resources.Identifier;
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
import team.tnt.collectorsalbum.common.init.MenuRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.util.PageInfo;

import java.util.List;

public class AlbumCategoryMenu extends AbstractContainerMenu {

    private PageInfo<Identifier> pageInfo;
    private final Int2IntMap cardNumberToSlotCache = new Int2IntOpenHashMap();
    private int cardSlotCount;

    public AlbumCategoryMenu(int menuId) {
        super(MenuRegistry.ALBUM_CATEGORY.get(), menuId);
    }

    public AlbumCategoryMenu(int menuId, Inventory playerInventory, PageInfo<Identifier> pageInfo) {
        this(menuId);
        this.pageInfo = pageInfo;
        this.cardSlotCount = 0;

        ItemStack itemStack = playerInventory.player.getMainHandItem();
        Album album = Album.fromItem(itemStack);
        if (album == null) {
            return;
        }

        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        PageInfo<AlbumCategory> page = manager.getPage(this.pageInfo);
        if (!page.isEmpty()) {
            AlbumCategory albumCategory = page.element();
            Identifier categoryIdentifier = albumCategory.identifier();
            AlbumCategoryUiTemplate template = albumCategory.visualTemplate();
            AlbumCategoryUiTemplate.SlotPositionTemplate slotTemplate = template.slotTemplate();
            int xStart = slotTemplate.xAlbumSlotPositionStart();
            int yStart = slotTemplate.yAlbumSlotPositionStart();
            int xSpacing = slotTemplate.xSlotSpacing();
            int ySpacing = slotTemplate.ySlotSpacing();
            int pageWidth = slotTemplate.pageWidth();
            int[] cardNumbers = albumCategory.getCardNumbers();
            int cardCount = albumCategory.getCardCount();
            int slotsPerPage = slotTemplate.columns() * slotTemplate.rows();
            int pageSize = albumCategory.getPageSize();
            int offset = this.pageInfo.getElementIndex(pageSize);
            Container wrapper = new AlbumInventoryWrapper(cardCount, itemStack, this.pageInfo, album);
            // left page cards
            for (int y = 0; y < slotTemplate.rows(); y++) {
                for (int x = 0; x < slotTemplate.columns(); x++) {
                    int cardX = xStart + x * xSpacing;
                    int cardY = yStart + y * ySpacing;
                    int cardIndex = offset + x + (y * slotTemplate.columns());
                    if (cardIndex >= cardCount)
                        break;
                    addSlot(new CardSlot(wrapper, cardIndex, cardX, cardY, categoryIdentifier, cardNumbers[cardIndex]));
                    this.cardNumberToSlotCache.put(cardNumbers[cardIndex], cardIndex % pageSize);
                    ++this.cardSlotCount;
                }
            }
            // right page cards
            if (cardCount >= slotsPerPage) {
                int cardsLength = slotTemplate.columns() * xSpacing - xSpacing;
                int rightStartX = pageWidth * 2 - 18 - cardsLength - xStart + 2;
                for (int y = 0; y < slotTemplate.rows(); y++) {
                    for (int x = 0; x < slotTemplate.columns(); x++) {
                        int cardX = rightStartX + x * xSpacing;
                        int cardY = yStart + y * ySpacing;
                        int cardIndex = offset + slotsPerPage + x + (y * slotTemplate.columns());
                        if (cardIndex >= cardCount)
                            break;
                        addSlot(new CardSlot(wrapper, cardIndex, cardX, cardY, categoryIdentifier, cardNumbers[cardIndex]));
                        this.cardNumberToSlotCache.put(cardNumbers[cardIndex], cardIndex % pageSize);
                        ++this.cardSlotCount;
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
        super.removed(player);
        CollectorsAlbum.forceAlbumReload(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            Identifier categoryId = this.pageInfo.element();
            AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
            AlbumCategory category = manager.find(this.pageInfo).orElse(null);
            if (category == null)
                return ItemStack.EMPTY;
            if (index >= 0 && index < this.cardSlotCount) {
                // Extraction
                if (!moveItemStackTo(slotItem, this.cardSlotCount, this.cardSlotCount + 36, true)) {
                    return ItemStack.EMPTY;
                }
                slot.setChanged();
            } else if (index >= this.cardSlotCount && index < this.cardSlotCount + 36) {
                // Insert to album
                AlbumCard albumCard = this.getCardInfo(slotItem);
                if (albumCard == null || !albumCard.category().equals(categoryId)) {
                    return this.transferInInventory(slotItem, index, this.cardSlotCount);
                }

                int cardNumber = albumCard.cardNumber();
                if (!this.cardNumberToSlotCache.containsKey(cardNumber)) {
                    return this.transferInInventory(slotItem, index, this.cardSlotCount);
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

    public PageInfo<Identifier> getPageInfo() {
        return this.pageInfo;
    }

    public AlbumCategory getCategory() {
        if (this.pageInfo.isEmpty()) {
            throw new IllegalStateException("Category is not defined");
        }
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        return manager.findById(this.pageInfo.element()).orElseThrow();
    }

    public int getPageIndex() {
        return this.pageInfo.pageIndex();
    }

    public int getPageCount() {
        return this.getCategory().getPageCount();
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

        private final Identifier category;
        private final int cardNumber;

        public CardSlot(Container container, int index, int slotX, int slotY, Identifier category, int number) {
            super(container, index, slotX, slotY);
            this.category = category;
            this.cardNumber = number;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            AlbumCardManager manager = AlbumCardManager.getInstance();
            return manager.getCardInfo(itemStack.getItem()).map(info -> {
                Identifier cardCategory = info.category();
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
        private final PageInfo<Identifier> pageInfo;

        AlbumInventoryWrapper(int size, ItemStack itemStack, PageInfo<Identifier> pageInfo, Album album) {
            super(size);
            this.itemStack = itemStack;
            this.pageInfo = pageInfo;

            List<ItemStack> itemStacks = album.getInventory(this.pageInfo.element());
            for (int i = 0; i < itemStacks.size(); i++) {
                ItemStack stack = itemStacks.get(i);
                if (!stack.isEmpty())
                    this.getItems().set(i, stack.copy());
            }
        }

        @Override
        public void setItem(int index, ItemStack itemStack) {
            Album album = Album.fromItem(this.itemStack);
            if (album != null) {
                Album.Mutable mutable = new Album.Mutable(album);
                mutable.set(this.pageInfo.element(), index, itemStack.copy());
                Album updated = mutable.toImmutable();
                updated.bind(this.itemStack);
            }
            super.setItem(index, itemStack);
        }

        @Override
        public void setChanged() {
            Album album = Album.fromItem(this.itemStack);
            if (album != null) {
                List<ItemStack> itemStacks = this.getItems();
                Album.Mutable mutableAlbum = new Album.Mutable(album);
                for (int i = 0; i < itemStacks.size(); i++) {
                    ItemStack stack = itemStacks.get(i);
                    mutableAlbum.set(this.pageInfo.element(), i, stack.copy());
                }
                mutableAlbum.buildAndBind(this.itemStack);
            }
            super.setChanged();
        }
    }
}
