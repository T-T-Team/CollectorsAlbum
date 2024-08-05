package team.tnt.collectorsalbum.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.card.CardRarity;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.init.ItemRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AlbumMainPageScreen extends Screen {

    public static final Component TITLE = Component.translatable("screen.collectorsalbum.album.main").withStyle(ChatFormatting.BOLD);
    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "textures/ui/album.png");
    public static final int TEXT_COLOR = 0x7B5C4C;
    private static final Component LABEL_RARITIES = Component.translatable("collectorsalbum.text.statistics.rarities_label").withStyle(ChatFormatting.UNDERLINE);
    private static final Component LABEL_CATEGORIES = Component.translatable("collectorsalbum.text.statistics.categories_label").withStyle(ChatFormatting.UNDERLINE);
    private static final String LANG_KEY_COLLECTED = "collectorsalbum.text.statistics.collected";
    private static final String LANG_KEY_POINTS = "collectorsalbum.text.statistics.points";
    private final ItemStack itemStack;
    protected int textureWidth = 256;
    protected int textureHeight = 256;
    protected int left, top;

    AlbumMainPageScreen(ItemStack itemStack) {
        super(TITLE);
        this.itemStack = itemStack;
    }

    public static List<BookmarkWidget> getBookmarks(int guiWidth, int guiHeight, int albumWidth, int albumHeight, int bookImageHeight) {
        int left = (guiWidth - albumWidth) / 2;
        int top = (guiHeight - albumHeight) / 2 + 10;
        int right = left + albumWidth;
        Duration tooltipDelay = Duration.ofSeconds(1);
        List<BookmarkWidget> bookmarks = new ArrayList<>();
        BookmarkWidget home = new BookmarkWidget(left - 32, top, 32, 18, true, ItemRegistry.ALBUM.get().getDefaultInstance(), () -> Minecraft.getInstance().screen instanceof AlbumMainPageScreen);
        home.setTooltip(Tooltip.create(TITLE));
        home.setTooltipDelay(tooltipDelay);
        home.setAction(AlbumNavigationHelper::navigateHomepage);

        BookmarkWidget bonuses = new BookmarkWidget(left - 32, top + 20, 32, 18, true, Items.EMERALD.getDefaultInstance(), () -> Minecraft.getInstance().screen instanceof AlbumBonusesScreen);
        bonuses.setTooltip(Tooltip.create(AlbumBonusesScreen.TITLE));
        bonuses.setTooltipDelay(tooltipDelay);
        bonuses.setAction(AlbumNavigationHelper::navigateBonusesPage);

        bookmarks.add(home);
        bookmarks.add(bonuses);

        List<AlbumCategory> categories = AlbumNavigationHelper.listCategoriesForBookmarks(bookImageHeight - 20);
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
        AlbumNavigationHelper.captureCurrentMousePositionSnapshot();
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
        List<BookmarkWidget> bookmarks = getBookmarks(width, height, textureWidth, textureHeight, 180);
        for (BookmarkWidget bookmark : bookmarks) {
            this.addRenderableWidget(bookmark);
        }

        // Background
        this.addRenderableOnly(new TextureRenderable(BACKGROUND, left, top, textureWidth, textureHeight));
        // Title
        int titleWidth = font.width(TITLE);
        this.addRenderableOnly(new LabelRenderable(TITLE, left + (128 - titleWidth) / 2, top + 14, TEXT_COLOR));

        Album album = this.itemStack.get(ItemDataComponentRegistry.ALBUM.get());
        if (album == null)
            return;
        AlbumCategoryManager categoryManager = AlbumCategoryManager.getInstance();

        // Collected cards info
        int collected = album.countCards();
        int available = categoryManager.getCollectibleCardCount();
        Component percent = Component.literal(String.format(Locale.ROOT, "(%.1f%%)", collected / (float) available * 100.0F));
        Component collectedCards = Component.translatable(LANG_KEY_COLLECTED, collected, available);
        this.addRenderableOnly(new LabelRenderable(collectedCards, left + 17, top + 30, TEXT_COLOR));
        this.addRenderableOnly(new LabelRenderable(percent, left + 17 + font.width(collectedCards) - font.width(percent), top + 40, TEXT_COLOR));

        // Rarity ratios
        Map<CardRarity, Album.CardRarityStatistics> ratios = album.calculateRarityRatios();
        this.addRenderableOnly(new LabelRenderable(LABEL_RARITIES, left + 17, top + 55, TEXT_COLOR));
        int rarityIndex = 0;
        for (CardRarity rarity : CardRarity.values()) {
            Album.CardRarityStatistics statistics = ratios.get(rarity);
            MutableComponent rarityText = Component.literal(rarity.getDisplayText().getString());
            rarityText.getStyle().applyFormats(ChatFormatting.RESET);
            String ratio = String.format(Locale.ROOT, "%.1f%%", statistics.getRatio() * 100.0F);
            Component label = Component.literal(rarityText.getString() + ": " + ratio);
            LabelWidget labelWidget = this.addRenderableWidget(new LabelWidget(left + 20, top + 65 + (rarityIndex++) * 10, font.width(label), 10, label, font, TEXT_COLOR));
            labelWidget.setTooltip(Tooltip.create(Component.literal(statistics.collected() + "/" + statistics.total()).withStyle(ChatFormatting.GREEN)));
        }

        // --- Right page
        // Points info
        Component points = Component.literal(String.valueOf(album.getPoints())).withColor(0xFFEFAE00).withStyle(ChatFormatting.BOLD);
        Component pointsLabel = Component.translatable(LANG_KEY_POINTS, points).withColor(TEXT_COLOR);
        this.addRenderableOnly(new LabelRenderable(pointsLabel, left + 145, top + 14, false, 0xFFFFFF));

        // Category information
        this.addRenderableOnly(new LabelRenderable(LABEL_CATEGORIES, left + 145, top + 30, false, TEXT_COLOR));
        List<Album.AlbumCategoryStatistics> statistics = album.calculateStatistics();
        for (int i = 0; i < Math.min(12, statistics.size()); i++) {
            Album.AlbumCategoryStatistics stat = statistics.get(i);
            MutableComponent categoryLabelNoStyle = Component.literal(stat.category().getDisplayText().getString());
            categoryLabelNoStyle.getStyle().applyFormats(ChatFormatting.RESET);
            Component displayLabel = Component.literal(categoryLabelNoStyle.getString() + ": " + stat.collectedCards() + "/" + stat.allCards());
            LabelWidget labelWidget = addRenderableWidget(new LabelWidget(left + 148, top + 40 + i * 10, 95, 10, displayLabel, font, TEXT_COLOR));
            labelWidget.setTooltip(Tooltip.create(Component.literal(String.format(Locale.ROOT, "%.1f%%", stat.getCollectedProgress() * 100F)).withStyle(ChatFormatting.GREEN)));
        }

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
