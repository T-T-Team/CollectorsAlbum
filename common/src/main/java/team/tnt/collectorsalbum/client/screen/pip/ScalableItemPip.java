package team.tnt.collectorsalbum.client.screen.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;

public final class ScalableItemPip extends PictureInPictureRenderer<ScalableItemRenderState> {

    private final ItemModelResolver itemModelResolver;
    private int code;

    public ScalableItemPip(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
        this.itemModelResolver = Minecraft.getInstance().getItemModelResolver();
    }

    @Override
    protected void renderToTexture(ScalableItemRenderState renderState, PoseStack poseStack) {
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        FeatureRenderDispatcher featureRenderDispatcher = gameRenderer.getFeatureRenderDispatcher();
        SubmitNodeStorage submitNodeStorage = featureRenderDispatcher.getSubmitNodeStorage();
        gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
        ItemStackRenderState itemStackRenderState = renderState.itemStackRenderState();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        this.itemModelResolver.updateForTopItem(itemStackRenderState, renderState.itemStack(), ItemDisplayContext.GUI, null, null, 0);
        itemStackRenderState.submit(poseStack, submitNodeStorage, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        featureRenderDispatcher.renderAllFeatures();
        this.code = renderState.code();
    }

    @Override
    protected boolean textureIsReadyToBlit(ScalableItemRenderState renderState) {
        return this.code == renderState.code();
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return height / 2.0F;
    }

    @Override
    protected String getTextureLabel() {
        return "collectorsalbum-scalable-item";
    }

    @Override
    public Class<ScalableItemRenderState> getRenderStateClass() {
        return ScalableItemRenderState.class;
    }
}
