package team.tnt.collectorsalbum.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.mixin.MouseHandlerAccessor;
import team.tnt.collectorsalbum.network.C2S_RequestAlbumCategoryInventory;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;

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
            long windowPtr = minecraft.getWindow().getWindow();
            accessor.setXpos(savedMouseX);
            accessor.setYpos(savedMouseY);
            GLFW.glfwSetCursorPos(windowPtr, savedMouseX, savedMouseY);
            GLFW.glfwSetInputMode(windowPtr, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            savedMouseX = null;
            savedMouseY = null;
        }
    }

    public static void storeItemStack(ItemStack itemStack) {
        lastItemStack = itemStack;
    }

    public static void navigateHomepage() {
        resetCategoryPage();
        Minecraft.getInstance().setScreen(new AlbumMainPageScreen(lastItemStack));
    }

    public static void navigateBonusesPage() {
        resetCategoryPage();
        Minecraft.getInstance().setScreen(new AlbumBonusesScreen(lastItemStack));
    }

    public static void navigateNextCategory() {
        AlbumCategory category = peekCategory(currentCategoryPage + 1);
        if (category != null) {
            PlatformNetworkManager.NETWORK.sendServerMessage(new C2S_RequestAlbumCategoryInventory(category.identifier()));
            ++currentCategoryPage;
        } else {
            navigateHomepage();
        }
    }

    public static void navigatePreviousCategory() {
        AlbumCategory category = peekCategory(currentCategoryPage - 1);
        if (category != null) {
            PlatformNetworkManager.NETWORK.sendServerMessage(new C2S_RequestAlbumCategoryInventory(category.identifier()));
            --currentCategoryPage;
        } else {
            navigateHomepage();
        }
    }

    public static void navigateCategory(AlbumCategory category) {
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        currentCategoryPage = manager.getPageForCategory(category);
        PlatformNetworkManager.NETWORK.sendServerMessage(new C2S_RequestAlbumCategoryInventory(category.identifier()));
    }

    public static Component getNextCategoryTitle() {
        AlbumCategory category = peekCategory(currentCategoryPage + 1);
        return category != null ? category.getDisplayText() : AlbumMainPageScreen.TITLE;
    }

    public static Component getPreviousCategoryTitle() {
        AlbumCategory category = peekCategory(currentCategoryPage - 1);
        return category != null ? category.getDisplayText() : AlbumMainPageScreen.TITLE;
    }

    public static boolean hasNextCategory() {
        return getNextCategoryTitle() != AlbumMainPageScreen.TITLE;
    }

    private static AlbumCategory peekCategory(int page) {
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        return manager.getCategoryForPage(page);
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

    public static void resetCategoryPage() {
        currentCategoryPage = -1;
    }
}
