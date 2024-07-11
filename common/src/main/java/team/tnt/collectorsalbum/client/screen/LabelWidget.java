package team.tnt.collectorsalbum.client.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class LabelWidget extends AbstractWidget {

    private final Font font;
    private final int textColor;
    private final boolean shadow;

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

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.drawString(font, this.getMessage(), getX(), getY(), textColor, shadow);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
