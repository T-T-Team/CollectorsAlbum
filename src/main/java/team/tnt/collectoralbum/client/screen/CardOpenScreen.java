package team.tnt.collectoralbum.client.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4f;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.common.item.ICard;
import team.tnt.collectoralbum.network.Networking;
import team.tnt.collectoralbum.network.packet.RequestCardPackDropPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class CardOpenScreen extends Screen {

    private static final ResourceLocation CARD_BACK = new ResourceLocation(CollectorsAlbum.MODID, "textures/screen/card_back.png");
    private final List<ITickableWidget> tickableWidgets = new ArrayList<>();
    private final List<ItemStack> drops;
    private int flipsRemaining;

    public CardOpenScreen(List<ItemStack> drops) {
        super(Component.literal("Card Open Screen"));
        this.drops = drops;
        Collections.shuffle(this.drops);
    }

    @Override
    protected void init() {
        tickableWidgets.clear();
        int total = drops.size();
        this.flipsRemaining = total;
        int deckWidth = (total - 1) * 65 + 64;
        int left = (width - deckWidth) / 2;
        int top = (height - 64) / 2;
        for (int i = 0; i < total; i++) {
            int desiredX = left + i * 65;
            CardWidget widget = addRenderableWidget(new CardWidget(width - 70 + i * 7, height - 70, 64, 64, desiredX, top, drops.get(i)));
            widget.setAnimTime(15);
            widget.setFlipTime(15);
            widget.setClickResponder(this::cardFlipped);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (flipsRemaining == 0) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
        boolean requireAll = CollectorsAlbum.config.requireTurnAllCards;
        if (!requireAll || flipsRemaining == 0) {
            Networking.dispatchServerPacket(new RequestCardPackDropPacket());
        }
    }

    @Override
    public void tick() {
        tickableWidgets.forEach(ITickableWidget::tickWidget);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        if (widget instanceof ITickableWidget tickable) {
            tickableWidgets.add(tickable);
        }
        return super.addRenderableWidget(widget);
    }

    private void cardFlipped(CardWidget widget) {
        --flipsRemaining;
    }

    private static final class CardWidget extends AbstractWidget implements ITickableWidget {

        private final int targetX;
        private final int targetY;
        private final int startX;
        private final int startY;
        private final ResourceLocation itemTexture;
        private final SoundEvent discoverySound;
        private int animTime = 30;
        private int timer, prevTimer;
        private Consumer<CardWidget> widgetConsumer = widget -> {
        };
        private boolean flipped;
        private boolean flipping;
        private int flipTimer, flipTimerOld, flipTimeTotal = 20;

        public CardWidget(int x, int y, int width, int height, int targetX, int targetY, ItemStack stack) {
            super(x, y, width, height, CommonComponents.EMPTY);
            this.startX = x;
            this.startY = y;
            this.targetX = targetX;
            this.targetY = targetY;
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
            this.itemTexture = new ResourceLocation(itemId.getNamespace(), "textures/item/" + itemId.getPath() + ".png");
            if (stack.getItem() instanceof ICard card) {
                this.discoverySound = card.getCardRarity().getDiscoverySound();
            } else {
                this.discoverySound = null;
            }
        }

        public void setAnimTime(int time) {
            this.animTime = time;
        }

        public void setFlipTime(int time) {
            this.flipTimeTotal = time;
        }

        public void setClickResponder(Consumer<CardWidget> widgetConsumer) {
            this.widgetConsumer = widgetConsumer;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            widgetConsumer.accept(this);
            flipping = true;
            if (discoverySound != null) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(discoverySound, 1.0F));
            }
        }

        @Override
        public void playDownSound(SoundManager handler) {
        }

        @Override
        protected boolean clicked(double mouseX, double mouseY) {
            return !flipped && !flipping && super.clicked(mouseX, mouseY);
        }

        @Override
        public void tickWidget() {
            updateMovementAnimationTimer();
            doFlipProcessTick();
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int scale = isHovered ? 8 : 0;
            float actual = timer / (float) animTime;
            float old = prevTimer / (float) animTime;
            float rawProgress = Mth.lerp(partialTick, old, actual);
            float progress = 1.0F - (float) Math.pow(1 - rawProgress, 3);
            int diffX = targetX - startX;
            int diffY = targetY - startY;
            float renderX = startX + (diffX * progress);
            float renderY = startY + (diffY * progress);
            setX((int) renderX);
            setY((int) renderY);
            Lighting.setupForFlatItems();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, flipped ? itemTexture : CARD_BACK);
            actuallyRender(graphics, renderX - scale, renderY - scale, renderX + width + scale, renderY + height + scale, partialTick);
        }

        private void actuallyRender(GuiGraphics graphics, float x1, float y1, float x2, float y2, float partialTicks) {
            if (flipping) {
                float flipActual = flipTimer / (float) flipTimeTotal;
                float flipOld = flipTimerOld / (float) flipTimeTotal;
                float flipProgressRaw = Mth.lerp(partialTicks, flipOld, flipActual);
                float flipProgress = flipProgressRaw < 0.5 ? 4 * flipProgressRaw * flipProgressRaw * flipProgressRaw : 1 - (float) Math.pow(-2 * flipProgressRaw + 2, 3) / 2;
                if (flipProgress >= 0.5) {
                    flipped = true;
                }
                float halfX = (x2 - x1) / 2.0F;
                float centerX = x1 + halfX;
                float progress = flipProgress < 0.5 ? 1.0F - (flipProgress / 0.5F) : (flipProgress - 0.5F) / 0.5F;
                x1 = centerX - halfX * progress;
                x2 = centerX + halfX * progress;
            }
            renderTexture(graphics, flipped ? itemTexture : CARD_BACK, (int) x1, (int) y1, 0, 0, 0, (int) (x2 - x1), (int) (y2 - y1), (int) (x2 - x1), (int) (y2 - y1));
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
        }

        private void updateMovementAnimationTimer() {
            prevTimer = timer;
            if (timer >= animTime) {
                return;
            }
            ++timer;
        }

        private void doFlipProcessTick() {
            if (!flipping) return;
            flipTimerOld = flipTimer;
            if (flipTimer >= flipTimeTotal) {
                return;
            }
            ++flipTimer;
        }
    }

    public static void renderTexture(Matrix4f pose, float x1, float y1, float x2, float y2) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(pose, x1, y1, 0).uv(0.0F, 0.0F).endVertex();
        bufferBuilder.vertex(pose, x1, y2, 0).uv(0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(pose, x2, y2, 0).uv(1.0F, 1.0F).endVertex();
        bufferBuilder.vertex(pose, x2, y1, 0).uv(1.0F, 0.0F).endVertex();
        tesselator.end();
        RenderSystem.disableBlend();
    }
}
