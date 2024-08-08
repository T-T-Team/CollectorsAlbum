package team.tnt.collectorsalbum.client.screen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumBonusDescriptionOutput;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumBonusManager;
import team.tnt.collectorsalbum.common.resource.bonus.AlbumBonus;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.time.Duration;
import java.util.List;

public class AlbumBonusesScreen extends Screen {

    public static final Component TITLE = Component.translatable("screen.collectorsalbum.album.bonuses").withStyle(ChatFormatting.BOLD);
    private static final int LINE_COUNT = 14;

    private final ItemStack itemStack;
    private int left, top;
    private int scrollingOffset, pageSize;

    private int currentBonusIndex;

    public AlbumBonusesScreen(ItemStack itemStack) {
        super(TITLE);
        this.itemStack = itemStack;
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
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderTransparentBackground(graphics);
    }

    @Override
    protected void init() {
        left = (this.width - 256) / 2;
        top = (this.height - 256) / 2;

        AlbumMainPageScreen.getBookmarks(width, height, 256, 256, 180).forEach(this::addRenderableWidget);
        this.addRenderableOnly(new TextureRenderable(AlbumMainPageScreen.BACKGROUND, left, top, 256, 256));

        Album album = this.itemStack.get(ItemDataComponentRegistry.ALBUM.get());
        if (album == null) {
            Component error = Component.literal("Failed to load album!").withStyle(ChatFormatting.RED);
            this.addRenderableOnly(new LabelRenderable(error, left + (256 - font.width(error)) / 2, top + (180 - font.lineHeight) / 2));
            return;
        }

        AlbumBonusManager manager = AlbumBonusManager.getInstance();
        Pair<AlbumBonus, AlbumBonus> bonusPair = manager.getBonusesForPage(currentBonusIndex);
        ActionContext context = ActionContext.of(ActionContext.PLAYER, minecraft.player, ActionContext.ALBUM, album);
        List<AlbumBonusDescriptionOutput.ComponentWithTooltip> first = this.getBonusDescription(bonusPair.getFirst(), context);
        List<AlbumBonusDescriptionOutput.ComponentWithTooltip> second = this.getBonusDescription(bonusPair.getSecond(), context);
        this.pageSize = Math.max(first.size(), second.size());
        this.createWidgets(first, left + 14, top + 14, this.scrollingOffset);
        this.createWidgets(second, left + 142, top + 14, this.scrollingOffset);

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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int scale = (int)(-scrollY);
        int next = this.scrollingOffset + scale;
        if (next >= 0 && next + LINE_COUNT <= this.pageSize) {
            this.scrollingOffset = next;
            this.init(this.minecraft, this.width, this.height);
            return true;
        } else {
            return false;
        }
    }

    private void createWidgets(List<AlbumBonusDescriptionOutput.ComponentWithTooltip> components, int x, int y, int scroll) {
        int offsetStep = 2;
        for (int i = scroll; i < Math.min(scroll + LINE_COUNT, components.size()); i++) {
            int index = i - scroll;
            AlbumBonusDescriptionOutput.ComponentWithTooltip component = components.get(i);
            int offset = offsetStep * component.level();
            Component label = component.label();
            LabelWidget widget = this.addRenderableWidget(new LabelWidget(x + offset, y + index * 10, 100 - offset, 10, label, font, AlbumMainPageScreen.TEXT_COLOR, true));
            if (component.hasTooltip()) {
                widget.setTooltip(Tooltip.create(component.tooltip()));
                widget.setTooltipDelay(Duration.ofMillis(400));
            }
        }
    }

    private List<AlbumBonusDescriptionOutput.ComponentWithTooltip> getBonusDescription(AlbumBonus bonus, ActionContext context) {
        AlbumBonusDescriptionOutput description = AlbumBonusDescriptionOutput.createEmpty(context);
        bonus.addDescription(description);
        return description.toComponentList();
    }

    private void addPage(int direction) {
        this.setPage(this.currentBonusIndex + direction);
    }

    private void setPage(int page) {
        this.currentBonusIndex = page;
        this.scrollingOffset = 0;
        init(minecraft, width, height);
    }
}
