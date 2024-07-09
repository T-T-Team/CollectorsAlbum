package team.tnt.collectorsalbum.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;

import java.util.function.BooleanSupplier;

public class BookmarkWidget extends AbstractWidget {

    private static final WidgetSprites LEFT_SIDE = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "textures/ui/bookmark_left.png"),
            ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "textures/ui/bookmark_disabled_left.png"),
            ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "textures/ui/bookmark_hovered_left.png")
    );
    private static final WidgetSprites RIGHT_SIDE = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "textures/ui/bookmark_right.png"),
            ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "textures/ui/bookmark_disabled_right.png"),
            ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "textures/ui/bookmark_hovered_right.png")
    );
    private final boolean leftside;
    private final ItemStack icon;
    private final BooleanSupplier isActive;
    private Runnable action;

    public BookmarkWidget(int x, int y, int width, int height, boolean leftside, ItemStack icon, BooleanSupplier isActive) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.leftside = leftside;
        this.icon = icon;
        this.isActive = isActive;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        WidgetSprites sprites = leftside ? LEFT_SIDE : RIGHT_SIDE;
        boolean hovered = isHovered();
        boolean active = isActive.getAsBoolean();
        ResourceLocation background = sprites.get(!active, hovered);
        guiGraphics.blit(background, getX(), getY(), 0, 0.0F, 0.0F, getWidth(), getHeight(), 32, 18);
        int hoverIconOffset = (hovered || active) ? -6 : 0;
        int baseIconOffset = leftside ? 15 : 0;
        guiGraphics.renderItem(this.icon, getX() + baseIconOffset + (leftside ? hoverIconOffset : -hoverIconOffset), getY() + 1);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void playDownSound(SoundManager $$0) {
        $$0.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    @Override
    public void onClick(double $$0, double $$1) {
        if (action != null)
            action.run();
    }
}
