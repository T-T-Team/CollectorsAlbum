package team.tnt.collectorsalbum.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.card.CardRarity;
import team.tnt.collectorsalbum.common.card.CardUiTemplate;
import team.tnt.collectorsalbum.common.card.RarityHolder;
import team.tnt.collectorsalbum.common.init.SoundRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.network.C2S_CompleteOpeningCardPack;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class CardPackOpeningScreen extends Screen {

    private static final Component TITLE = Component.translatable("screen.collectorsalbum.card_pack_opening_screen");
    private static final ResourceLocation CARD_BG = new ResourceLocation(CollectorsAlbum.MOD_ID, "textures/ui/card_back.png");
    private static final int CARD_SIZE = 32;
    private static final int CARD_MARGIN = 8;

    private final FxEmitter emitter;
    private final List<ItemStack> drops;
    private final List<CardWidget> cardWidgets = new ArrayList<>();

    public CardPackOpeningScreen(List<ItemStack> drops) {
        super(TITLE);
        this.emitter = new FxEmitter();
        this.drops = drops;
    }

    @Override
    protected void init() {
        super.init();
        this.cardWidgets.clear();

        int rows = Mth.ceil(drops.size() / 6.0F);
        int containerHeight = rows * (CARD_SIZE + CARD_MARGIN) - CARD_MARGIN;
        int containerTop = (this.height - containerHeight) / 2;
        for (int y = 0; y < rows; y++) {
            int rowCols = Math.min(6, this.drops.size() - y * 6);
            int rowWidth = rowCols * (CARD_SIZE + CARD_MARGIN) - CARD_MARGIN;
            int rowLeft = (this.width - rowWidth) / 2;
            int rowTop = containerTop + y * (CARD_SIZE + CARD_MARGIN);
            for (int x = 0; x < rowCols; x++) {
                int index = x + y * 6;
                ItemStack drop = this.drops.get(index);
                CardWidget widget = this.addCardWidget(rowLeft + x * (CARD_SIZE + CARD_MARGIN), rowTop, drop);
                widget.setOnIconFlipped(this::onCardFlipped);
                widget.setPositionAnimationDelay(index * 3);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics);
        this.emitter.draw(graphics, delta);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void removed() {
        PlatformNetworkManager.NETWORK.sendServerMessage(new C2S_CompleteOpeningCardPack());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        this.emitter.update();
        this.cardWidgets.forEach(CardWidget::tick);
    }

    private void onCardFlipped(CardWidget widget) {
        AlbumCard card = widget.card;
        CardUiTemplate template = card.template();
        Integer[] effects = template.effectColors();
        Integer[] durations = template.effectDurations();
        double speed = 4.0;
        int count = Math.min(20 + card.getPoints() * 5, 100) / Math.max(effects.length, 1);
        for (int i = 0; i < effects.length; i++) {
            float centerX = widget.getX() + widget.getWidth() / 2.0F;
            float centerY = widget.getY() + widget.getHeight() / 2.0F;
            this.emitter.emit(centerX, centerY, effects[i], durations[i], count, speed);
        }
    }

    private CardWidget addCardWidget(int x, int y, ItemStack itemStack) {
        int left = (this.width - CARD_SIZE) / 2;
        int top = this.height - CARD_SIZE / 2;
        CardWidget widget = new CardWidget(left, top, CARD_SIZE, CARD_SIZE, x, y, itemStack);
        this.cardWidgets.add(widget);
        return this.addRenderableWidget(widget);
    }

    static void renderFullTexture(ResourceLocation path, Matrix4f pose, float x, float y, float z, float width, float height) {
        RenderSystem.setShaderTexture(0, path);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(pose, x, y, z).uv(0.0F, 0.0F).endVertex();
        builder.vertex(pose, x, y + height, z).uv(0.0F, 1.0F).endVertex();
        builder.vertex(pose, x + width, y + height, z).uv(1.0F, 1.0F).endVertex();
        builder.vertex(pose, x + width, y, z).uv(1.0F, 0.0F).endVertex();
        BufferUploader.drawWithShader(builder.end());
    }

    static void renderFullColoredTexture(ResourceLocation path, Matrix4f pose, float x, float y, float z, float width, float height, int color) {
        RenderSystem.setShaderTexture(0, path);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        builder.vertex(pose, x, y, z).uv(0.0F, 0.0F).color(color).endVertex();
        builder.vertex(pose, x, y + height, z).uv(0.0F, 1.0F).color(color).endVertex();
        builder.vertex(pose, x + width, y + height, z).uv(1.0F, 1.0F).color(color).endVertex();
        builder.vertex(pose, x + width, y, z).uv(1.0F, 0.0F).color(color).endVertex();
        BufferUploader.drawWithShader(builder.end());
    }

    private static final class CardWidget extends AbstractWidget {

        private static final int TOTAL_MOVE_TIME = 10;
        private static final int TOTAL_FLIP_TIME = 20;

        private final int originalX, originalY;
        private final int targetX, targetY;
        private final AlbumCard card;
        private final ResourceLocation itemTexture;
        private Consumer<CardWidget> onIconFlipped;
        private Consumer<CardWidget> onFlipFinish;

        private boolean flipped;
        private boolean flipping;
        private int flipCurrent, flipOld;
        private int positionCurrent, positionOld;
        private int positionAnimationDelay;

        public CardWidget(int x, int y, int width, int height, int targetX, int targetY, ItemStack itemStack) {
            super(x, y, width, height, CommonComponents.EMPTY);
            this.originalX = x;
            this.originalY = y;
            this.targetX = targetX;
            this.targetY = targetY;
            this.card = AlbumCardManager.getInstance().getCardInfo(itemStack.getItem())
                    .orElse(null);
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
            this.itemTexture = new ResourceLocation(itemId.getNamespace(), "textures/item/" + itemId.getPath() + ".png"); // not the best way to do it, but should work in most cases
        }

        public void setOnIconFlipped(Consumer<CardWidget> onIconFlipped) {
            this.onIconFlipped = onIconFlipped;
        }

        public void setOnFlipFinish(Consumer<CardWidget> onFlipFinish) {
            this.onFlipFinish = onFlipFinish;
        }

        public void setPositionAnimationDelay(int positionAnimationDelay) {
            this.positionAnimationDelay = positionAnimationDelay;
        }

        public void tick() {
            this.tickPosition();
            this.tickFlipping();
        }

        @Override
        public void playDownSound(SoundManager manager) {
            SoundEvent event;
            if (this.card instanceof RarityHolder holder) {
                CardRarity rarity = holder.rarity();
                event = rarity.getFlipSoundRef().get();
            } else {
                event = SoundRegistry.FLIP_COMMON.get();
            }
            manager.play(SimpleSoundInstance.forUI(event, 1.0F));
        }

        @Override
        public void onClick(double $$0, double $$1) {
            if (this.flipping || this.flipped)
                return;
            this.flipping = true;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
            this.updatePosition(deltaTicks);
            this.renderCardWithFlipAnimation(graphics, deltaTicks);
        }

        private void updatePosition(float delta) {
            if (this.positionOld == this.positionCurrent && this.positionCurrent > 0) {
                this.setX(targetX);
                this.setY(targetY);
                return;
            }
            float positionAnimProgress = Mth.lerp(delta, this.positionOld, this.positionCurrent) / (float) TOTAL_MOVE_TIME;
            float positionAnimEased = 1.0F - (float) Math.pow(1.0F - positionAnimProgress, 3);
            int xDelta = this.targetX - this.originalX;
            int yDelta = this.targetY - this.originalY;
            this.setX((int) (this.originalX + xDelta * positionAnimEased));
            this.setY((int) (this.originalY + yDelta * positionAnimEased));
        }

        private void renderCardWithFlipAnimation(GuiGraphics graphics, float delta) {
            float px = this.getX();
            int py = this.getY();
            float pWidth = this.getWidth();
            int pHeight = this.getHeight();

            if (this.flipping) {
                boolean wasFlipped = this.flipped;
                float flipActual = this.flipCurrent / (float) TOTAL_FLIP_TIME;
                float flipOld = this.flipOld / (float) TOTAL_FLIP_TIME;
                float flipProgressRaw = Mth.lerp(delta, flipOld, flipActual);
                float flipProgress = flipProgressRaw < 0.5 ? 4 * flipProgressRaw * flipProgressRaw * flipProgressRaw : 1 - (float) Math.pow(-2 * flipProgressRaw + 2, 3) / 2;
                if (flipProgress >= 0.5) {
                    this.flipped = true;
                }
                float amount = this.flipped ? (flipProgress - 0.5F) / 0.5F : 1.0F - (flipProgress / 0.5F);
                float halfWidth = this.getWidth() / 2.0F;
                px = this.getX() + halfWidth * (1.0F - amount);
                pWidth = this.getWidth() * amount;
                if (this.flipped && !wasFlipped && this.onIconFlipped != null && this.card != null) {
                    this.onIconFlipped.accept(this);
                }
            }

            ResourceLocation texture = this.flipped ? itemTexture : CARD_BG;
            Matrix4f poseMat = graphics.pose().last().pose();
            renderFullTexture(texture, poseMat, px, py, 400, pWidth, pHeight);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }

        @Override
        protected boolean clicked(double mouseX, double mouseY) {
            return !this.flipped && !this.flipping && super.clicked(mouseX, mouseY);
        }

        private void tickPosition() {
            if (this.positionAnimationDelay > 0) {
                this.positionAnimationDelay--;
                return;
            }
            this.positionOld = this.positionCurrent;
            if (this.positionCurrent < TOTAL_MOVE_TIME) {
                if (this.positionCurrent++ == 0) {
                    SoundManager manager = Minecraft.getInstance().getSoundManager();
                    manager.play(SimpleSoundInstance.forUI(SoundRegistry.FLIP_COMMON.get(), 1.5F, 0.25F));
                }
            }
        }

        private void tickFlipping() {
            if (!this.flipping)
                return;
            this.flipOld = this.flipCurrent;
            if (this.flipCurrent >= TOTAL_FLIP_TIME) {
                this.flipping = false;
                if (this.onFlipFinish != null && this.card != null) {
                    this.onFlipFinish.accept(this);
                }
                return;
            }
            ++this.flipCurrent;
        }
    }

    private static class FxEmitter {

        private final List<FxElement> liveElements = new ArrayList<>();

        void emit(float x, float y, int color, int lifetime, int count, double dirMul) {
            for (int i = 0; i < count; i++) {
                FxElement element = new FxElement(x, y, color, lifetime);
                double xRand = Math.random() - Math.random();
                double yRand = Math.random() - Math.random();
                element.setDir(new Vector2d(xRand * dirMul, yRand * dirMul));
                liveElements.add(element);
            }
        }

        void update() {
            Iterator<FxElement> it = liveElements.iterator();
            while (it.hasNext()) {
                FxElement element = it.next();
                element.update();
                if (element.shouldRemove()) {
                    it.remove();
                }
            }
        }

        void draw(GuiGraphics graphics, float delta) {
            this.liveElements.forEach(fx -> fx.draw(graphics, delta));
        }
    }

    private static class FxElement {

        private static final ResourceLocation[] PATHS = {
                spark(0), spark(1), spark(2), spark(3),
                spark(4), spark(5), spark(6), spark(7)
        };

        private float x, y, xOld, yOld;
        private final int color;
        private final int stageLife;
        private int stage;
        private int stageLifeLeft;
        private Vector2d direction;

        public FxElement(float x, float y, int color, int stageLife) {
            this.x = x;
            this.y = y;
            this.xOld = x;
            this.yOld = y;
            this.color = color;
            this.stageLife = stageLife;
            this.stageLifeLeft = stageLife;
            this.stage = PATHS.length - 1;
        }

        void draw(GuiGraphics graphics, float delta) {
            ResourceLocation path = PATHS[this.stage];
            Matrix4f pose = graphics.pose().last().pose();
            float scale = 8.0F;
            float halfScale = scale / 2.0F;
            float xPos = Mth.lerp(delta, xOld, x);
            float yPos = Mth.lerp(delta, yOld, y);
            renderFullColoredTexture(path, pose, xPos - halfScale, yPos - halfScale, 0, scale, scale, 0xFF << 24 | this.color);
        }

        void update() {
            this.xOld = this.x;
            this.yOld = this.y;
            if (--this.stageLifeLeft < 0) {
                --this.stage;
                this.stageLifeLeft = this.stageLife;
            }
            if (this.direction != null) {
                this.x += (float) this.direction.x;
                this.y += (float) this.direction.y;
                this.direction.mul(0.95F);
            }
        }

        void setDir(Vector2d dir) {
            this.direction = dir;
        }

        boolean shouldRemove() {
            return this.stage == 0 && this.stageLifeLeft <= 0;
        }

        private static ResourceLocation spark(int index) {
            return new ResourceLocation(String.format("textures/particle/spark_%d.png", index));
        }
    }
}
