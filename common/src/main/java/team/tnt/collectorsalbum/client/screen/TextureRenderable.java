package team.tnt.collectorsalbum.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.resources.ResourceLocation;

public class TextureRenderable implements Renderable {

    private final ResourceLocation resourceLocation;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public TextureRenderable(ResourceLocation resourceLocation, int x, int y, int width, int height) {
        this.resourceLocation = resourceLocation;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.blit(resourceLocation, x, y, 0, 0, width, height);
    }
}
