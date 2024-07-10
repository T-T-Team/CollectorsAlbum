package team.tnt.collectorsalbum.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2d;
import team.tnt.collectorsalbum.common.AlbumCard;
import team.tnt.collectorsalbum.common.card.CardRarity;

import java.util.ArrayList;
import java.util.List;

// TODO implement
public class CardPackOpeningScreen extends Screen {

    private static final Component TITLE = Component.translatable("screen.collectorsalbum.card_pack_opening_screen");

    private final FxEmitter emitter;

    public CardPackOpeningScreen(List<ItemStack> drops) {
        super(TITLE);
        this.emitter = new FxEmitter();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public void tick() {
        super.tick();
    }

    private void onCardFlipped(AlbumCard card) {
        CardRarity rarity = card.rarity();
        double speed = 0.3;
        switch (rarity) {
            case COMMON -> emitter.emit(0xFFFFFF, 50, 15, speed);
            case UNCOMMON -> emitter.emit(0xFFFFFF, 50, 25, speed);
            case RARE -> emitter.emit(0xFFFFFF, 50, 30, speed);
            case EPIC -> emitter.emit(0xFFFFFF, 50, 40, speed);
            case LEGENDARY -> emitter.emit(0xFFFFFF, 50, 50, speed);
            case MYTHICAL -> {
                emitter.emit(0xFFFFFF, 50, 30, speed);
                emitter.emit(0xFF0000, 50, 30, speed);
            }
        }
        this.emitter.emit(0xFFFFFFF, 50, 15, 0.5);
    }

    private static class FxEmitter {

        private List<FxElement> liveElements = new ArrayList<>();

        void emit(int color, int lifetime, int count, double dirMul) {
            for (int i = 0; i < count; i++) {
                FxElement element = new FxElement(color, lifetime);
                double xRand = Math.random() - Math.random();
                double yRand = Math.random() - Math.random();
                element.setDir(new Vector2d(xRand, yRand), dirMul);
                liveElements.add(element);
            }
        }
    }

    private static class FxElement {

        private final int color;
        private int lifetime;

        public FxElement(int color, int lifetime) {
            this.color = color;
            this.lifetime = lifetime;
        }

        void draw(GuiGraphics graphics) {

        }

        void update() {

        }

        void setDir(Vector2d dir, double speed) {

        }
    }
}
