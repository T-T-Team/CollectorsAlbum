package team.tnt.collectorsalbum.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.AlbumCategoryUiTemplate;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;

import java.time.Duration;
import java.util.List;

public class AlbumCategoryScreen extends AbstractContainerScreen<AlbumCategoryMenu> {

    private final AlbumCategory category;
    private final List<Slot> cardSlots;

    public AlbumCategoryScreen(AlbumCategoryMenu menu, Inventory inventory, Component title, AlbumCategory category) {
        super(menu, inventory, title);
        this.category = category;
        AlbumCategoryUiTemplate template = category.visualTemplate();
        this.imageWidth = template.backgroundTexture.textureWidth();
        this.imageHeight = template.backgroundTexture.textureHeight();
        this.cardSlots = menu.slots.stream().filter(slot -> !(slot.container instanceof Inventory)).toList();
    }

    public AlbumCategory getCategory() {
        return category;
    }

    @Override
    protected void init() {
        super.init();
        AlbumNavigationHelper.restoreMousePositionFromSnapshot();

        PageButton prevPage = addRenderableWidget(new PageButton(leftPos + 22, topPos + 156, false, btn -> AlbumNavigationHelper.navigatePreviousCategory(), true));
        prevPage.setTooltip(Tooltip.create(AlbumNavigationHelper.getPreviousCategoryTitle()));
        prevPage.setTooltipDelay(1000);
        if (AlbumNavigationHelper.hasNextCategory()) {
            PageButton nextPage = addRenderableWidget(new PageButton(leftPos + 210, topPos + 156, true, btn -> AlbumNavigationHelper.navigateNextCategory(), true));
            nextPage.setTooltip(Tooltip.create(AlbumNavigationHelper.getNextCategoryTitle()));
            nextPage.setTooltipDelay(1000);
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
        int width = font.width(category.getDisplayText());
        graphics.drawString(font, category.getDisplayText(), (imageWidth - width) / 2, -15, 0xFFFFFF, false);
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
                    Component num = Component.literal("#" + cardNumbers[slot]);
                    PoseStack pose = guiGraphics.pose();
                    pose.pushPose();
                    pose.translate(leftPos + cardSlot.x + 1, topPos + cardSlot.y + 1, 0);
                    pose.scale(0.75F, 0.75F, 0.75F);
                    guiGraphics.drawString(font, num, 0, 0, template.slotCardNumberTextColor, false);
                    pose.popPose();
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
