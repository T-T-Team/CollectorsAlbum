package team.tnt.collectorsalbum.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class LabelWidget extends AbstractWidget {

    private final Font font;
    private final int textColor;
    private final boolean shadow;

    private OverflowRender overflowRender;

    public LabelWidget(int x, int y, int width, int height, Component label, Font font, int textColor, boolean shadow) {
        super(x, y, width, height, label);
        this.font = font;
        this.textColor = textColor;
        this.shadow = shadow;
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return false;
    }

    public void setOverflowRendering(int background, int foreground, int textBackground) {
        this.overflowRender = new OverflowRender(background, textBackground, foreground);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        int textWidth = font.width(this.getMessage());
        if (overflowRender != null && isHovered && textWidth > this.width) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(0, 0, 400);
            this.overflowRender.render(guiGraphics, this.getX() - 2, this.getY() - 2, textWidth + 2, 11);
            guiGraphics.drawString(font, this.getMessage(), getX(), getY(), textColor, shadow);
            poseStack.popPose();
        } else {
            guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
            guiGraphics.drawString(font, this.getMessage(), getX(), getY(), textColor, shadow);
            guiGraphics.disableScissor();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    private record OverflowRender(int backgroundDark, int background, int backgroundLight) {

        public void render(GuiGraphics guiGraphics, int x, int y, int width, int height) {
            guiGraphics.fill(x, y, x + width + 1, y + height + 1, backgroundDark);
            guiGraphics.fill(x + 1, y + 1, x + width + 1, y + height + 1, backgroundLight);
            guiGraphics.fill(x + 1, y + 1, x + width, y + height, background);
        }
    }
}
