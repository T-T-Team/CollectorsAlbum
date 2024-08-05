package team.tnt.collectorsalbum.client.screen;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.Objects;

public class LabelWidget extends AbstractWidget {

    private final Font font;
    private final int textColor;
    private final boolean scrolling;

    public LabelWidget(int x, int y, int width, int height, Component label, Font font, int textColor, boolean scrolling) {
        super(x, y, width, height, label);
        this.font = font;
        this.textColor = textColor;
        this.scrolling = scrolling;
    }

    public LabelWidget(int x, int y, int width, int height, Component label, Font font, int textColor) {
        this(x, y, width, height, label, font, textColor, false);
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return false;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
        if (scrolling) {
            renderScrollingStringInternal(guiGraphics, font, this.getMessage(), this.getX(), this.getX(), this.getY(), this.getRight(), this.getBottom(), textColor);
        } else {
            guiGraphics.drawString(font, this.getMessage(), this.getX(), this.getY(), textColor, false);
        }
        guiGraphics.disableScissor();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    private static void renderScrollingStringInternal(GuiGraphics graphics, Font font, Component label, int textX, int x, int y, int width, int height, int color) {
        int textWidth = font.width(label);
        int containerHeight = y + height;
        Objects.requireNonNull(font);
        int posY = (containerHeight - 9) / 2 + 1;
        int containerWidth = width - x;
        int $$12;
        if (textWidth > containerWidth) {
            $$12 = textWidth - containerWidth;
            double $$13 = (double) Util.getMillis() / 1000.0;
            double $$14 = Math.max((double)$$12 * 0.5, 3.0);
            double $$15 = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * $$13 / $$14)) / 2.0 + 0.5;
            double $$16 = Mth.lerp($$15, 0.0, $$12);
            graphics.enableScissor(x, y, width, height);
            graphics.drawString(font, label, x - (int)$$16, posY, color, false);
            graphics.disableScissor();
        } else {
            graphics.drawString(font, label, textX, posY, color, false);
        }

    }
}
