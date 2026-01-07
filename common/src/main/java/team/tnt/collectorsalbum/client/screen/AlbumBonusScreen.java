package team.tnt.collectorsalbum.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumBonusManager;
import team.tnt.collectorsalbum.common.resource.bonus.AlbumBonus;
import team.tnt.collectorsalbum.common.resource.bonus.BonusHolder;
import team.tnt.collectorsalbum.common.resource.bonus.IntermediateAlbumBonus;
import team.tnt.collectorsalbum.common.resource.bonus.NoBonus;
import team.tnt.collectorsalbum.common.resource.bonus.AlbumBonusDetail;
import team.tnt.collectorsalbum.common.resource.bonus.SectionOutput;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.util.Hierarchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.UnaryOperator;

public class AlbumBonusScreen extends Screen {

    public static final Component TITLE = Component.translatable("screen.collectorsalbum.album.bonuses");
    private static final Component DESCRIPTION_1 = Component.translatable("screen.collectorsalbum.album.bonuses.description_1");
    private static final Component DESCRIPTION_2 = Component.translatable("screen.collectorsalbum.album.bonuses.description_2");
    private final ItemStack itemStack; // album itemstack

    private int selectedBonus = -1;
    private int sidebarWidth;
    private Component sidebarTitle;
    private final List<FormattedCharSequence> mainPageText = new ArrayList<>();
    private List<BonusHolder> holders;

    private int sidebarScroll = 0;

    public AlbumBonusScreen(ItemStack itemStack) {
        super(TITLE.copy().withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE));
        this.itemStack = itemStack;
    }

    @Override
    protected void init() {
        this.initSidebar();

        Album album = this.itemStack.get(ItemDataComponentRegistry.ALBUM.get());
        if (album == null && this.selectedBonus != -1) {
            this.selectPage(-1);
            return;
        }
        if (this.isMainPageView()) {
            this.initMainPage();
        } else {
            this.initBonusPage(album);
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float deltaTick) {
        graphics.fill(0, 0, this.width, this.height, 0x99 << 24);
        graphics.fill(0, 0, this.sidebarWidth, this.height, 0x44 << 24);
        graphics.drawString(this.font, this.sidebarTitle, 5, 5, 0xFFFFFFFF);

        if (this.isMainPageView()) {
            // render main page info
            graphics.drawString(this.font, this.title, this.sidebarWidth + 5, 5, 0xFFFFFFFF);

            for (int i = 0; i < this.mainPageText.size(); i++) {
                FormattedCharSequence text = this.mainPageText.get(i);
                graphics.drawString(this.font, text, this.sidebarWidth + 5, 40 + i * 9, 0xFFCCCCCC);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        if (mouseX > this.sidebarWidth) {
            return false;
        }
        int amount = (int)(-scrollY);
        int displaySize = (this.height - 20) / 10;
        int next = this.sidebarScroll + amount;
        int max = this.holders.size() - displaySize;
        if (next >= 0 && next <= max) {
            this.sidebarScroll = next;
            return true;
        }
        return false;
    }

    private boolean isMainPageView() {
        return this.selectedBonus == -1;
    }

    private void initMainPage() {
        int availableWidth = this.width - this.sidebarWidth - 10;
        this.mainPageText.clear();
        this.mainPageText.addAll(this.font.split(DESCRIPTION_1, availableWidth));
        this.mainPageText.add(CommonComponents.EMPTY.getVisualOrderText());
        this.mainPageText.addAll(this.font.split(DESCRIPTION_2, availableWidth));
    }

    private void initSidebar() {
        this.sidebarWidth = Math.min(this.width / 3, 128);
        AlbumBonusManager manager = AlbumBonusManager.getInstance();
        this.holders = manager.listAllBonuses();
        this.sidebarTitle = Component.translatable("screen.collectorsalbum.album.bonuses.sidebar_title", this.holders.size()).withStyle(ChatFormatting.UNDERLINE);

        int maxBonusesPerPage = (this.height - 20) / 10;
        for (int i = this.sidebarScroll; i < Math.min(this.sidebarScroll + maxBonusesPerPage, this.holders.size()); i++) {
            int renderIndex = i - this.sidebarScroll;
            BonusHolder holder = this.holders.get(i);
            BonusButton bonusButton = this.addRenderableWidget(new BonusButton(0, 20 + renderIndex * 10, this.sidebarWidth, 10, i, holder.title(), this.font));
            bonusButton.onSelected(this::selectPage);
            bonusButton.setSelected(this.selectedBonus == i);
        }
    }

    private void initBonusPage(Album album) {
        BonusHolder holder = this.holders.get(Mth.clamp(this.selectedBonus, 0, this.holders.size() - 1));
        AlbumBonusDetail.Output outputWriter = new AlbumBonusDetail.Output(holder);
        ActionContext context = ActionContext.of(ActionContext.PLAYER, this.minecraft.player, ActionContext.ALBUM, album);
        Hierarchy<AlbumBonus> tree = Hierarchy.of(holder.value(), val -> val instanceof IntermediateAlbumBonus intermediate ? intermediate.children() : Collections.emptyList());
        List<Hierarchy.Node<AlbumBonus>> bonuses = tree.getLeafNodes().stream()
                .filter(node -> node.value() != NoBonus.INSTANCE)
                .toList();
        for (Hierarchy.Node<AlbumBonus> bonusNode : bonuses) {
            outputWriter.section(output -> {
                SectionOutput activeOutput = output;
                List<AlbumBonus> flatTree = bonusNode.toFlatDependencyTree();
                for (int i = 0; i < flatTree.size(); i++) {
                    AlbumBonus node = flatTree.get(i);
                    if (node instanceof IntermediateAlbumBonus intermediate) {
                        activeOutput = intermediate.appendItemDetailsWithModifiers(activeOutput, context, flatTree.get(i + 1)); // should be safe as all intermediate nodes should have child node
                    } else {
                        node.appendDetails(activeOutput, context);
                    }
                }
            });
        }
        AlbumBonusDetail detail = outputWriter.build();
        this.addRenderableWidget(new BonusDetail(this.sidebarWidth, 0, this.width - this.sidebarWidth - 10, this.height, this.font, detail));
    }

    private void selectPage(int page) {
        this.selectedBonus = page != this.selectedBonus ? page : -1;
        this.init(this.minecraft, this.width, this.height);
    }

    private static final class BonusButton extends AbstractWidget {

        private final int index;
        private final Font font;

        private IntConsumer onSelection;
        private boolean selected;

        public BonusButton(int x, int y, int width, int height, int index, Component title, Font font) {
            super(x, y, width, height, title);
            this.index = index;
            this.font = font;
        }

        public void onSelected(IntConsumer event) {
            this.onSelection = event;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
            if (this.selected || this.isHovered) {
                guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), 0x44FFFFFF);
            }
            renderScrollingString(guiGraphics, this.font, this.getMessage(), 0, this.getX() + 5, this.getY(), this.getRight() - 10, this.getBottom(), 0xFFFFFFFF);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (this.onSelection != null)
                this.onSelection.accept(this.index);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }

    private static final class BonusDetail extends AbstractWidget {

        public static final int DESCRIPTION_TEXT_COLOR = 0xFFCCCCCC;
        private final Font font;
        private final AlbumBonusDetail detail;

        private final List<FormattedCharSequence> compiledText = new ArrayList<>();
        private final int maxLines;
        private int scrollOffset;

        public BonusDetail(int x, int y, int width, int height, Font font, AlbumBonusDetail detail) {
            super(x, y, width, height, detail.title());
            this.font = font;
            this.detail = detail;

            this.maxLines = (height - 12) / 12 + 1;
            this.compileText();
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            for (int line = this.scrollOffset; line < Math.min(this.scrollOffset + this.maxLines, this.compiledText.size()); line++) {
                FormattedCharSequence text = this.compiledText.get(line);
                int lineIndex = line - this.scrollOffset;
                graphics.drawString(this.font, text, this.getX() + 5, this.getY() + 5 + lineIndex * 12, 0xFFFFFFFF);
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
            int scale = (int)(-scrollY);
            int next = this.scrollOffset + scale;
            int maxScroll = this.compiledText.size() - this.maxLines;
            if (next >= 0 && next <= maxScroll) {
                this.scrollOffset = next;
                return true;
            }
            return false;
        }

        private void addText(Component text, UnaryOperator<Style> style) {
            this.compiledText.addAll(this.font.split(text.copy().withStyle(style), this.getWidth()));
        }

        private void addText(Component text) {
            this.addText(text, style -> style);
        }

        private void addDetailText(Component text) {
            this.addText(text, style -> style.withColor(DESCRIPTION_TEXT_COLOR));
        }

        private void newLine(int count) {
            for (int i = 0; i < count; i++) {
                this.addText(CommonComponents.space());
            }
        }

        private void compileText() {
            this.compileHeader();
            this.newLine(2);

            for (AlbumBonusDetail.Section section : this.detail.sections()) {
                this.compileSection(section);
                this.newLine(1);
                this.addDetailText(Component.literal("----------------------------------------"));
                this.newLine(1);
            }
        }

        private void compileHeader() {
            this.addText(this.detail.title(), style -> style.applyFormats(ChatFormatting.BOLD, ChatFormatting.UNDERLINE));
            this.newLine(1);
            this.addDetailText(this.detail.description());
            this.newLine(1);

            if (this.detail.hasAdditionalInfo()) {
                List<Component> additionalInfo = detail.additionalInfo();
                additionalInfo.forEach(this::addDetailText);
                this.newLine(1);
            }
        }

        private void compileSection(AlbumBonusDetail.Section section) {
            this.addText(section.title(), style -> style.applyFormats(ChatFormatting.BOLD));

            List<Component> description = section.description();
            if (!description.isEmpty()) {
                description.forEach(this::addDetailText);
                this.newLine(1);
            }

            List<AlbumBonusDetail.ConditionDetail> conditions = section.conditions();
            if (!conditions.isEmpty()) {
                int total = conditions.size();
                int matching = (int) conditions.stream().filter(AlbumBonusDetail.ConditionDetail::fulfilled).count();
                this.addText(Component.translatable("collectorsalbum.label.bonus.conditions", matching, total), style -> style.applyFormats(ChatFormatting.UNDERLINE).withColor(DESCRIPTION_TEXT_COLOR));
                for (AlbumBonusDetail.ConditionDetail conditionDetail : conditions) {
                    Component conditionText = conditionDetail.title().copy().withStyle(conditionDetail.fulfilled() ? ChatFormatting.GREEN : ChatFormatting.RED);
                    Component prefix = Component.literal("- ").withColor(DESCRIPTION_TEXT_COLOR).append(conditionText);
                    this.addText(prefix);
                }
            }
        }
    }
}
