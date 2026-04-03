package team.tnt.collectorsalbum.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class TextureRenderable implements Renderable {

    private final Identifier resourceLocation;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public TextureRenderable(Identifier resourceLocation, int x, int y, int width, int height) {
        this.resourceLocation = resourceLocation;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED, this.resourceLocation,
                this.x, this.y,
                0.0F, 0.0F,
                this.width, this.height,
                256, 256
        );
    }
}
