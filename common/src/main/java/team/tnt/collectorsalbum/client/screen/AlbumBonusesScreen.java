package team.tnt.collectorsalbum.client.screen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import team.tnt.collectorsalbum.common.resource.AlbumBonusManager;
import team.tnt.collectorsalbum.common.resource.bonus.AlbumBonus;

import java.time.Duration;

public class AlbumBonusesScreen extends Screen {

    public static final Component TITLE = Component.translatable("screen.collectorsalbum.album.bonuses").withStyle(ChatFormatting.BOLD);

    private int left, top;

    private int currentBonusIndex;

    public AlbumBonusesScreen() {
        super(TITLE);
    }

    @Override
    public void removed() {
        AlbumNavigationHelper.captureCurrentMousePositionSnapshot();
    }

    @Override
    protected void init() {
        left = (this.width - 256) / 2;
        top = (this.height - 256) / 2;

        AlbumMainPageScreen.getBookmarks(width, height, 256, 256).forEach(this::addRenderableWidget);
        this.addRenderableOnly(new TextureRenderable(AlbumMainPageScreen.BACKGROUND, left, top, 256, 256));

        AlbumBonusManager manager = AlbumBonusManager.getInstance();
        Pair<AlbumBonus, AlbumBonus> bonusPair = manager.getBonusesForPage(currentBonusIndex);
        AlbumBonus first = bonusPair.getFirst();
        AlbumBonus second = bonusPair.getSecond();
        // TODO for each bonus compile description and add as a widget

        if (currentBonusIndex > 0) {
            // previous page btn
            PageButton prevPage = addRenderableWidget(new PageButton(left + 22, top + 156, false, btn -> addPage(-1), true));
            prevPage.setTooltip(Tooltip.create(AlbumNavigationHelper.getPreviousCategoryTitle()));
            prevPage.setTooltipDelay(Duration.ofSeconds(1));
        }
        if (manager.hasNextPage(currentBonusIndex)) {
            // next page btn
            PageButton nextPage = addRenderableWidget(new PageButton(left + 210, top + 156, true, btn -> addPage(1), true));
            nextPage.setTooltip(Tooltip.create(AlbumNavigationHelper.getNextCategoryTitle()));
            nextPage.setTooltipDelay(Duration.ofSeconds(1));
        }
    }

    private void addPage(int direction) {
        this.setPage(this.currentBonusIndex + direction);
    }

    private void setPage(int page) {
        this.currentBonusIndex = page;
        init(minecraft, width, height);
    }
}
