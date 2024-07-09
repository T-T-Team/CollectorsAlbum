package team.tnt.collectorsalbum.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.init.ItemRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AlbumMainPageScreen extends Screen {

    public static final Component TITLE = Component.translatable("screen.collectorsalbum.album.main").withStyle(ChatFormatting.BOLD);
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "textures/ui/album.png");
    private final ItemStack itemStack;
    protected int textureWidth = 256;
    protected int textureHeight = 256;
    protected int left, top;

    AlbumMainPageScreen(ItemStack itemStack) {
        super(TITLE);
        this.itemStack = itemStack;
    }

    public static List<BookmarkWidget> getBookmarks(int guiWidth, int guiHeight, int albumWidth, int albumHeight) {
        int left = (guiWidth - albumWidth) / 2;
        int top = (guiHeight - albumHeight) / 2 + 10;
        int right = left + albumWidth;
        Duration tooltipDelay = Duration.ofSeconds(1);
        List<BookmarkWidget> bookmarks = new ArrayList<>();
        BookmarkWidget home = new BookmarkWidget(left - 32, top, 32, 18, true, ItemRegistry.ALBUM.get().getDefaultInstance(), () -> Minecraft.getInstance().screen instanceof AlbumMainPageScreen);
        home.setTooltip(Tooltip.create(TITLE));
        home.setTooltipDelay(tooltipDelay);
        home.setAction(AlbumNavigationHelper::navigateHomepage);

        bookmarks.add(home);

        List<AlbumCategory> categories = AlbumNavigationHelper.listCategoriesForBookmarks(albumHeight - 20);
        int index = 0;
        for (AlbumCategory category : categories) {
            BookmarkWidget categoryBookmark = new BookmarkWidget(right, top + (index++) * 20, 32, 18, false, category.visualTemplate().bookmarkIcon, () -> Minecraft.getInstance().screen instanceof AlbumCategoryScreen catScreen && catScreen.getCategory().identifier().equals(category.identifier()));
            categoryBookmark.setTooltip(Tooltip.create(category.getDisplayText()));
            categoryBookmark.setTooltipDelay(tooltipDelay);
            categoryBookmark.setAction(() -> AlbumNavigationHelper.navigateCategory(category));
            bookmarks.add(categoryBookmark);
        }

        return bookmarks;
    }

    @Override
    public void removed() {
        AlbumNavigationHelper.saveMousePositionSnapshot();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        this.left = (this.width - this.textureWidth) / 2;
        this.top = (this.height - this.textureHeight) / 2;

        this.addDefaultWidgets();
    }

    protected void addDefaultWidgets() {
        List<BookmarkWidget> bookmarks = getBookmarks(width, height, textureWidth, textureHeight);
        for (BookmarkWidget bookmark : bookmarks) {
            this.addRenderableWidget(bookmark);
        }

        final int textColor = 0x7B5C4C;
        // Background
        this.addRenderableOnly(new TextureRenderable(BACKGROUND, left, top, textureWidth, textureHeight));
        // Title
        int titleWidth = font.width(TITLE);
        this.addRenderableOnly(new LabelRenderable(TITLE, left + (128 - titleWidth) / 2, top + 14, textColor));
        // Collected cards info

        // Rarity ratios graph

        // Points info

        // --- Right page
        // Category information

        // Page button
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        AlbumCategory category = manager.getCategoryForPage(0);
        if (category != null) {
            PageButton button = this.addRenderableWidget(new PageButton(left + 210, top + 156, true, btn -> AlbumNavigationHelper.navigateNextCategory(), true));
            button.setTooltip(Tooltip.create(AlbumNavigationHelper.getNextCategoryTitle()));
            button.setTooltipDelay(Duration.ofSeconds(1));
        }
    }
}
