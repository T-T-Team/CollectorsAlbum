package team.tnt.collectorsalbum.common.menu;

import net.minecraft.core.NonNullList;
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
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.init.MenuRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;

import java.util.List;

public class AlbumCategoryMenu extends AbstractContainerMenu {

    private ResourceLocation category;

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
            int categoryCards = albumCategory.getSlots();
            int slotsPerPage = slotTemplate.colums() * slotTemplate.rows();
            Container wrapper = new AlbumInventoryWrapper(categoryCards, itemStack, category, album);
            // left page cards
            for (int y = 0; y < slotTemplate.rows(); y++) {
                for (int x = 0; x < slotTemplate.colums(); x++) {
                    int cardX = xStart + x * xSpacing;
                    int cardY = yStart + y * ySpacing;
                    int cardIndex = x + (y * slotTemplate.colums());
                    if (cardIndex >= categoryCards)
                        break;
                    addSlot(new CardSlot(wrapper, cardIndex, cardX, cardY, category));
                }
            }
            // right page cards
            if (categoryCards >= slotsPerPage) {
                int cardsLength = slotTemplate.colums() * xSpacing - xSpacing;
                int rightStartX = pageWidth * 2 - 18 - cardsLength - xStart + 2;
                for (int y = 0; y < slotTemplate.rows(); y++) {
                    for (int x = 0; x < slotTemplate.colums(); x++) {
                        int cardX = rightStartX + x * xSpacing;
                        int cardY = yStart + y * ySpacing;
                        int cardIndex = slotsPerPage + x + (y * slotTemplate.colums());
                        if (cardIndex >= categoryCards)
                            break;
                        addSlot(new CardSlot(wrapper, cardIndex, cardX, cardY, category));
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
        return ItemStack.EMPTY; // TODO implement
    }

    public AlbumCategory getCategory() {
        if (category == null) {
            throw new IllegalStateException("Category is not defined");
        }
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        return manager.findById(category).orElseThrow();
    }

    private static final class CardSlot extends Slot {

        private final ResourceLocation category;

        public CardSlot(Container container, int index, int slotX, int slotY, ResourceLocation category) {
            super(container, index, slotX, slotY);
            this.category = category;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            AlbumCardManager manager = AlbumCardManager.getInstance();
            return manager.getCardInfo(itemStack.getItem()).map(info -> {
                ResourceLocation cardCategory = info.category();
                int cardNumber = info.cardNumber();
                return cardNumber == (this.index + 1) && cardCategory.equals(category);
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

        AlbumInventoryWrapper(int size, ItemStack itemStack, ResourceLocation category, Album album) {
            super(size);

            List<ItemStack> itemStacks = album.getInventory(category);
            for (int i = 0; i < itemStacks.size(); i++) {
                ItemStack stack = itemStacks.get(i);
                if (!stack.isEmpty())
                    this.getItems().set(i, stack.copy());
            }

            this.addListener(container -> {
                Album currentAlbum = itemStack.get(ItemDataComponentRegistry.ALBUM.get());
                if (currentAlbum != null) {
                    NonNullList<ItemStack> updatedItems = ((SimpleContainer) container).getItems();
                    Album newAlbumInstance = currentAlbum.update(category, updatedItems);
                    itemStack.set(ItemDataComponentRegistry.ALBUM.get(), newAlbumInstance);
                }
            });
        }
    }
}
