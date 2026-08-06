package team.tnt.collectorsalbum.client.platform;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;

public interface ClientPlatform {

    void submitPictureInPictureRenderState(GuiGraphicsExtractor graphics, PictureInPictureRenderState state);
}
