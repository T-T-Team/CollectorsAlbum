package team.tnt.collectorsalbum.client.platform;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;

public final class NeoforgeClientPlatform implements ClientPlatform {

    @Override
    public void submitPictureInPictureRenderState(GuiGraphicsExtractor graphics, PictureInPictureRenderState state) {
        graphics.submitPictureInPictureRenderState(state);
    }
}
