package team.tnt.collectorsalbum.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.AlbumCategoryUiTemplate;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;

import java.time.Duration;
import java.util.List;

public class AlbumCategoryScreen extends AbstractContainerScreen<AlbumCategoryMenu> {

    private final AlbumCategory category;
    private final List<Slot> cardSlots;
    private final ItemStack itemStack;

    public AlbumCategoryScreen(AlbumCategoryMenu menu, Inventory inventory, Component title, AlbumCategory category) {
        super(menu, inventory, title);
        this.category = category;
        AlbumCategoryUiTemplate template = category.visualTemplate();
        this.imageWidth = template.backgroundTexture.textureWidth();
        this.imageHeight = template.backgroundTexture.textureHeight();
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
    protected void renderLabels(GuiGraphics graphics, int $$1, int $$2) {
        Component categoryLabel = category.getDisplayText();
        Style displayTextStyle = categoryLabel.getStyle().withItalic(true);
        MutableComponent displayLabel = categoryLabel.copy().withStyle(ChatFormatting.BOLD);
        int width = font.width(displayLabel);
        graphics.drawString(font, displayLabel, (imageWidth - width) / 2, -25, 0xFFFFFF, false);

        Album album = Album.fromItem(this.itemStack);
        if (album != null) {
            int points = album.getCardsForCategory(this.category.identifier()).stream().mapToInt(AlbumCard::getPoints).sum();
            Component pointLabel = AlbumMainPageScreen.getPointLabel(points).withStyle(displayTextStyle);
            graphics.drawString(font, pointLabel, (imageWidth - font.width(pointLabel)) / 2, -15, 0xFFFFFF, false);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        AlbumCategoryUiTemplate template = category.visualTemplate();
        blitTextureTemplate(guiGraphics, leftPos, topPos, template.backgroundTexture);
        int[] cardNumbers = category.getCardNumbers();
        if (template.renderSlots) {
            for (int slot = 0; slot < cardSlots.size(); slot++) {
                Slot cardSlot = cardSlots.get(slot);
                blitTextureTemplate(guiGraphics, leftPos + cardSlot.x - 1, topPos + cardSlot.y - 1, template.slotTexture);
                if (template.renderSlotCardNumbers && !cardSlot.hasItem()) {
                    Component num = Component.literal(template.cardNumberPrefix + cardNumbers[slot]);
                    PoseStack pose = guiGraphics.pose();
                    guiGraphics.enableScissor(leftPos + cardSlot.x, topPos + cardSlot.y, leftPos + cardSlot.x + template.slotTexture.width() - 2, topPos + cardSlot.y + template.slotTexture.height() - 2);
                    pose.pushPose();
                    pose.translate(leftPos + cardSlot.x + 1, topPos + cardSlot.y + 1, 0);
                    pose.scale(0.75F, 0.75F, 0.75F);
                    guiGraphics.drawString(font, num, 0, 0, template.slotCardNumberTextColor, false);
                    pose.popPose();
                    guiGraphics.disableScissor();
                }
            }
        }
    }

    public static void blitTextureTemplate(GuiGraphics graphics, int x, int y, AlbumCategoryUiTemplate.TextureTemplate template) {
        int texWidth = template.textureWidth();
        int texHeight = template.textureHeight();
        float u = template.texU() / (float) texWidth;
        float v = template.texV() / (float) texHeight;
        graphics.blit(template.resource(), x, y, 0, u, v, template.width(), template.height(), texWidth, texHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
