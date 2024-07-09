package team.tnt.collectorsalbum.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;

public class LabelRenderable implements Renderable {

    private final Component label;
    private final int x;
    private final int y;
    private final boolean shadow;
    private final int textColor;

    public LabelRenderable(Component label, int x, int y) {
        this(label, x, y, false);
    }

    public LabelRenderable(Component label, int x, int y, int textColor) {
        this(label, x, y, false, textColor);
    }

    public LabelRenderable(Component label, int x, int y, boolean shadow) {
        this(label, x, y, shadow, 0xFFFFFF);
    }

    public LabelRenderable(Component label, int x, int y, boolean shadow, int textColor) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.shadow = shadow;
        this.textColor = textColor;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, this.label, this.x, this.y, this.textColor, this.shadow);
    }
}
