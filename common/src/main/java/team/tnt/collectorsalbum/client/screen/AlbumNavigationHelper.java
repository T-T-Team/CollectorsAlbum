package team.tnt.collectorsalbum.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.sdl.SDLMouse;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.mixin.MouseHandlerAccessor;
import team.tnt.collectorsalbum.network.C2S_RequestAlbumCategoryInventory;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;
import team.tnt.collectorsalbum.util.PageInfo;

import java.util.Collections;
import java.util.List;

public final class AlbumNavigationHelper {

    private static ItemStack lastItemStack = ItemStack.EMPTY;
    private static int currentCategoryPage = -1;
    private static Double savedMouseX;
    private static Double savedMouseY;

    public static void captureCurrentMousePositionSnapshot() {
        MouseHandlerAccessor accessor = (MouseHandlerAccessor) Minecraft.getInstance().mouseHandler;
        savedMouseX = accessor.getXpos();
        savedMouseY = accessor.getYpos();
    }

    public static void restoreMousePositionFromSnapshot() {
        Minecraft minecraft = Minecraft.getInstance();
        MouseHandlerAccessor accessor = (MouseHandlerAccessor) minecraft.mouseHandler;
        if (savedMouseX != null && savedMouseY != null) {
            long windowPtr = minecraft.getWindow().handle();
            accessor.setXpos(savedMouseX);
            accessor.setYpos(savedMouseY);
            SDLMouse.SDL_WarpMouseInWindow(windowPtr, savedMouseX.intValue(), savedMouseY.intValue());
            savedMouseX = null;
            savedMouseY = null;
        }
    }

    public static void storeItemStack(ItemStack itemStack) {
        lastItemStack = itemStack;
    }

    public static void navigateHomepage() {
        resetPage();
        Minecraft.getInstance().gui.setScreen(new AlbumMainPageScreen(lastItemStack));
    }

    public static void navigateBonusesPage() {
        resetPage();
        Minecraft.getInstance().gui.setScreen(new AlbumBonusScreen(lastItemStack));
    }

    public static ItemStack getStoredAlbum() {
        return lastItemStack;
    }

    public static void navigateToNextPage() {
        navigateToPage(1);
    }

    public static void navigateToPreviousPage() {
        navigateToPage(-1);
    }

    public static void navigateToBookmark(AlbumCategory category) {
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        currentCategoryPage = manager.getPageForCategory(category);
        PageInfo<Identifier> pageInfo = PageInfo.firstPage(category.identifier());
        PlatformNetworkManager.NETWORK.sendServerMessage(new C2S_RequestAlbumCategoryInventory(pageInfo));
    }

    public static Component getNextPageTitle() {
        PageInfo<AlbumCategory> category = peekPage(currentCategoryPage + 1);
        return !category.isEmpty() ? category.mapElement(AlbumCategory::getDisplayText) : AlbumMainPageScreen.TITLE;
    }

    public static Component getPreviousPageTitle() {
        PageInfo<AlbumCategory> category = peekPage(currentCategoryPage - 1);
        return !category.isEmpty() ? category.mapElement(AlbumCategory::getDisplayText) : AlbumMainPageScreen.TITLE;
    }

    public static boolean hasNextPage() {
        return getNextPageTitle() != AlbumMainPageScreen.TITLE;
    }

    public static List<AlbumCategory> listCategoriesForBookmarks(int height) {
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        List<AlbumCategory> list = manager.listBookmarkableCategories();
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        int bookmarkCount = height / 20;
        int halfCount = bookmarkCount / 2;
        int from = Math.max(0, currentCategoryPage - halfCount);
        int to = Math.min(list.size(), currentCategoryPage + halfCount + 1);

        if (to - from > bookmarkCount) {
            to = from + bookmarkCount;
        }
        if (to - from < bookmarkCount && to == list.size()) {
            from = Math.max(0, to - bookmarkCount);
        }

        int size = to - from;
        if (size < Math.min(bookmarkCount, list.size())) {
            to = Math.min(list.size(), from + bookmarkCount);
        }

        return list.subList(from, to);
    }

    public static void resetPage() {
        currentCategoryPage = -1;
    }

    private static void navigateToPage(int direction) {
        PageInfo<AlbumCategory> page = peekPage(currentCategoryPage + direction);
        if (page.isEmpty()) {
            navigateHomepage();
        } else {
            PlatformNetworkManager.NETWORK.sendServerMessage(new C2S_RequestAlbumCategoryInventory(page.map(AlbumCategory::identifier)));
            currentCategoryPage += direction;
        }
    }

    private static PageInfo<AlbumCategory> peekPage(int page) {
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        return manager.getCategoryForPage(page);
    }
}
