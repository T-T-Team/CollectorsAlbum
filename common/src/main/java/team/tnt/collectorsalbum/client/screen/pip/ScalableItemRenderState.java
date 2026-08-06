package team.tnt.collectorsalbum.client.screen.pip;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record ScalableItemRenderState(
        ItemStackRenderState itemStackRenderState,
        ItemStack itemStack,
        int x0, int y0,
        int x1, int y1,
        float scale,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds,
        int code) implements PictureInPictureRenderState {
}
