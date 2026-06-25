package team.tnt.collectorsalbum.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.AlbumCategoryUiTemplate;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;

import java.time.Duration;
import java.util.List;

public class AlbumCategoryScreen extends AbstractContainerScreen<AlbumCategoryMenu> {

    private final AlbumCategory category;
    private final List<Slot> cardSlots;
    private final ItemStack itemStack;

    public AlbumCategoryScreen(AlbumCategoryMenu menu, Inventory inventory, Component title, AlbumCategory category) {
        AlbumCategoryUiTemplate template = category.visualTemplate();
        super(menu, inventory, title, template.backgroundTexture.textureWidth(), template.backgroundTexture.textureHeight());
        this.category = category;
        this.cardSlots = menu.slots.stream().filter(slot -> !(slot.container instanceof Inventory)).toList();
        this.itemStack = AlbumNavigationHelper.getStoredAlbum();
    }

    public AlbumCategory getCategory() {
        return category;
    }

    @Override
    protected void init() {
        super.init();
        AlbumNavigationHelper.restoreMousePositionFromSnapshot();

        Album album = Album.fromItem(this.itemStack);
        if (album == null) {
            CollectorsAlbum.LOGGER.error("Failed to open album category {} due to invalid album item: {}", category.identifier(), itemStack);
            AlbumNavigationHelper.navigateHomepage();
            return;
        }

        PageButton prevPage = addRenderableWidget(new PageButton(leftPos + 22, topPos + 156, false, btn -> AlbumNavigationHelper.navigatePreviousCategory(), true));
        prevPage.setTooltip(Tooltip.create(AlbumNavigationHelper.getPreviousCategoryTitle()));
        prevPage.setTooltipDelay(Duration.ofSeconds(1));
        if (AlbumNavigationHelper.hasNextCategory()) {
            PageButton nextPage = addRenderableWidget(new PageButton(leftPos + 210, topPos + 156, true, btn -> AlbumNavigationHelper.navigateNextCategory(), true));
            nextPage.setTooltip(Tooltip.create(AlbumNavigationHelper.getNextCategoryTitle()));
            nextPage.setTooltipDelay(Duration.ofSeconds(1));
        }
        AlbumCategoryUiTemplate template = category.visualTemplate();
        AlbumMainPageScreen.getBookmarks(width, height, imageWidth, imageHeight, template.bookImageHeight).forEach(this::addRenderableWidget);
    }

    @Override
    public void removed() {
        AlbumNavigationHelper.captureCurrentMousePositionSnapshot();
        super.removed();
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int $$1, int $$2) {
        Component categoryLabel = category.getDisplayText();
        Style displayTextStyle = categoryLabel.getStyle().withItalic(true);
        MutableComponent displayLabel = categoryLabel.copy().withStyle(ChatFormatting.BOLD);
        int width = font.width(displayLabel);
        graphics.text(font, displayLabel, (imageWidth - width) / 2, -25, 0xFFFFFFFF, false);

        Album album = Album.fromItem(this.itemStack);
        if (album != null) {
            int points = album.getCardsForCategory(this.category.identifier()).stream().mapToInt(AlbumCard::getPoints).sum();
            Component pointLabel = AlbumMainPageScreen.getPointLabel(points).withStyle(displayTextStyle);
            graphics.text(font, pointLabel, (imageWidth - font.width(pointLabel)) / 2, -15, 0xFFFFFFFF, false);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.extractTransparentBackground(graphics);
        AlbumCategoryUiTemplate template = category.visualTemplate();
        blitTextureTemplate(graphics, leftPos, topPos, template.backgroundTexture);
        int[] cardNumbers = category.getCardNumbers();
        if (template.renderSlots) {
            for (int slot = 0; slot < cardSlots.size(); slot++) {
                Slot cardSlot = cardSlots.get(slot);
                blitTextureTemplate(graphics, leftPos + cardSlot.x - 1, topPos + cardSlot.y - 1, template.slotTexture);
                if (template.renderSlotCardNumbers && !cardSlot.hasItem()) {
                    Component num = Component.literal(template.cardNumberPrefix + cardNumbers[slot]);
                    Matrix3x2fStack pose = graphics.pose();
                    graphics.enableScissor(leftPos + cardSlot.x, topPos + cardSlot.y, leftPos + cardSlot.x + template.slotTexture.width() - 2, topPos + cardSlot.y + template.slotTexture.height() - 2);
                    pose.pushMatrix();
                    pose.translate(leftPos + cardSlot.x + 1, topPos + cardSlot.y + 1);
                    pose.scale(0.75F, 0.75F);
                    graphics.text(font, num, 0, 0, ARGB.opaque(template.slotCardNumberTextColor), false);
                    pose.popMatrix();
                    graphics.disableScissor();
                }
            }
        }
    }

    public static void blitTextureTemplate(GuiGraphicsExtractor graphics, int x, int y, AlbumCategoryUiTemplate.TextureTemplate template) {
        int texWidth = template.textureWidth();
        int texHeight = template.textureHeight();
        float u = template.texU() / (float) texWidth;
        float v = template.texV() / (float) texHeight;
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, template.resource(),
                x, y,
                u, v,
                template.width(), template.height(),
                texWidth, texHeight
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        this.extractTooltip(graphics, mouseX, mouseY);
    }
}
