package team.tnt.collectoralbum.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.common.init.CardCategoryRegistry;
import team.tnt.collectoralbum.network.Networking;
import team.tnt.collectoralbum.network.packet.RequestAlbumPagePacket;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AlbumScreen extends Screen {

    private static final ResourceLocation TITLE = new ResourceLocation(CollectorsAlbum.MODID, "textures/screen/album_title.png");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(CollectorsAlbum.MODID, "textures/screen/album.png");
    private static final ResourceLocation ARROW_LEFT = new ResourceLocation(CollectorsAlbum.MODID, "textures/screen/album_previous.png");
    private static final ResourceLocation ARROW_RIGHT = new ResourceLocation(CollectorsAlbum.MODID, "textures/screen/album_next.png");

    // Localizations
    private static final MutableComponent TEXT_HEADER = Component.translatable("text.collectorsalbum.album.header").withStyle(ChatFormatting.BOLD);
    private static final MutableComponent TEXT_CATEGORIES = Component.translatable("text.collectorsalbum.album.categories").withStyle(ChatFormatting.UNDERLINE);
    private static final MutableComponent TEXT_RARITIES = Component.translatable("text.collectorsalbum.album.rarities").withStyle(ChatFormatting.UNDERLINE);
    private static final Function<Integer, MutableComponent> TEXT_POINTS = points -> Component.translatable("text.collectorsalbum.album.points", points);
    private static final BiFunction<Integer, Integer, MutableComponent> TEXT_TOTAL_CARDS = (cards, total) -> Component.translatable("text.collectorsalbum.album.total_cards", cards, total);

    private final int pageIndex;

    public AlbumScreen() {
        super(Component.translatable("screen.collectorsalbum.album"));
        this.pageIndex = 0;
    }

    @Override
    protected void init() {
        /*if (pageIndex > 0) {
            ArrowWidget widget = addRenderableWidget(new ArrowWidget(leftPos + 18, topPos + 5, 16, 16, ARROW_LEFT));
            widget.setOnClickResponder(this::clickPrevPage);
        }
        if (pageIndex < CardCategoryRegistry.getCount()) {
            ArrowWidget widget = addRenderableWidget(new ArrowWidget(leftPos + 265, topPos + 4, 16, 16, ARROW_RIGHT));
            widget.setOnClickResponder(this::clickNextPage);
        }
        this.stats = menu.getContainer().getStats();*/
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_D || keyCode == GLFW.GLFW_KEY_RIGHT) {
            changePage(1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_A || keyCode == GLFW.GLFW_KEY_LEFT) {
            changePage(-1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    //@Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        /*RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, this.menu.isTitle() ? TITLE : BACKGROUND);
        Matrix4f pose = graphics.pose().last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(pose, leftPos, topPos, 0).uv(0.0F, 0.0F).endVertex();
        bufferBuilder.vertex(pose, leftPos, topPos + imageHeight, 0).uv(0.0F, 1.0F).endVertex();
        bufferBuilder.vertex(pose, leftPos + imageWidth, topPos + imageHeight, 0).uv(1.0F, 1.0F).endVertex();
        bufferBuilder.vertex(pose, leftPos + imageWidth, topPos, 0).uv(1.0F, 0.0F).endVertex();
        tesselator.end()*/
    }

    //@Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        /*if (this.menu.isTitle()) {
            // left page
            // header
            int headerWidth = font.width(TEXT_HEADER);
            graphics.drawString(font, TEXT_HEADER.getString(), 20 + (130 - headerWidth) / 2.0F, 13, 0x7C5D4D, false);
            // rarity pcts
            graphics.drawString(font, TEXT_RARITIES, 27, 55, 0x7C5D4D, false);
            int i = 0;
            Map<CardRarity, Integer> byRarity = stats.getCardsByRarity();
            for (CardRarity rarity : CardRarity.values()) {
                int value = byRarity.getOrDefault(rarity, 0);
                String name = rarity.name();
                String pct = Math.round(value / (float) stats.getCardsCollected() * 100) + "%";
                String text = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1).toLowerCase() + ": " + pct;
                graphics.drawString(font, text, 30, 67 + i++ * 10, 0x7C5D4D, false);
            }
            // total cards
            int collected = stats.getCardsCollected();
            int total = stats.getTotalCards();
            graphics.drawString(font, TEXT_TOTAL_CARDS.apply(collected, total), 27, 35, 0x7C5D4D, false);
            // points
            int points = stats.getPoints();
            graphics.drawString(font, TEXT_POINTS.apply(points), 27, 80 + i * 10, 0x7C5D4D, false);

            // right page
            graphics.drawString(font, TEXT_CATEGORIES, 164, 35, 0x7C5D4D, false);
            int j = 0;
            Map<ICardCategory, List<ICard>> map = stats.getCardsByCategory();
            for (ICardCategory category : CardCategoryRegistry.getValues().stream().sorted().toArray(ICardCategory[]::new)) {
                int value = Optional.ofNullable(map.get(category)).map(List::size).orElse(0);
                Component displayName = category.getTranslatedName();
                String count = value + " / " + category.getCapacity();
                String text = displayName.getString() + " - " + count;
                graphics.drawString(font, text, 167, 47 + j++ * 10, 0x7C5D4D, false);
            }
            return;
        }
        for (Slot slot : this.menu.slots) {
            if (slot instanceof AlbumMenu.CardSlot cardSlot) {
                int cardNumber = cardSlot.getCardNumber();
                String text = "#" + cardNumber;
                graphics.drawString(font, text, slot.x + (18 - font.width(text)) / 2.0F - 1, slot.y + 18, 0x7C5D4D, false);
            }
        }
        ICardCategory category = menu.getCategory();
        MutableComponent component = Component.literal(category.getTranslatedName().getString()).withStyle(ChatFormatting.ITALIC);
        graphics.drawString(font, component, 40, 10, 0x7C5D4D, false);*/
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    protected void clickPrevPage(ArrowWidget widget) {
        changePage(-1);
    }

    protected void clickNextPage(ArrowWidget widget) {
        changePage(1);
    }

    protected void changePage(int indexOffset) {
        int nextIndex = this.pageIndex + indexOffset;
        if (nextIndex < 0 || nextIndex > CardCategoryRegistry.getCount()) return;
        // TODO
        //ICardCategory category = nextIndex == 0 ? null : CardCategoryRegistry.byIndex(nextIndex - 1);
        //Networking.dispatchServerPacket(new RequestAlbumPagePacket(category));
    }

    protected static final class ArrowWidget extends AbstractWidget {

        private final ResourceLocation location;
        private ClickResponder clickResponder = widget -> {
        };

        public ArrowWidget(int x, int y, int width, int height, ResourceLocation location) {
            super(x, y, width, height, CommonComponents.EMPTY);
            this.location = location;
        }

        public void setOnClickResponder(ClickResponder responder) {
            this.clickResponder = responder;
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, location);
            Matrix4f pose = graphics.pose().last().pose();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.vertex(pose, getX(), getY(), 0).uv(0.0F, 0.0F).endVertex();
            bufferBuilder.vertex(pose, getX(), getY() + width, 0).uv(0.0F, 1.0F).endVertex();
            bufferBuilder.vertex(pose, getX() + height, getY() + width, 0).uv(1.0F, 1.0F).endVertex();
            bufferBuilder.vertex(pose, getX() + height, getY(), 0).uv(1.0F, 0.0F).endVertex();
            tesselator.end();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            clickResponder.onClick(this);
        }

        @FunctionalInterface
        interface ClickResponder {
            void onClick(ArrowWidget widget);
        }
    }
}
