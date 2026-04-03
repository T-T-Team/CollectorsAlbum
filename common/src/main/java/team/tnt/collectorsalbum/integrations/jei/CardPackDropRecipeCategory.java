package team.tnt.collectorsalbum.integrations.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawablesView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import org.jetbrains.annotations.Nullable;
import team.tnt.collectorsalbum.common.init.ItemRegistry;
import team.tnt.collectorsalbum.common.resource.CardPackDropManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CardPackDropRecipeCategory implements IRecipeCategory<CardPackDropManager.DropEntry> {

    public static final Component TITLE = Component.translatable("collectorsalbum.label.jei.pack_drop.title");

    private final IJeiHelpers helpers;
    private final IDrawable icon;

    public CardPackDropRecipeCategory(IJeiHelpers helpers) {
        this.helpers = helpers;
        this.icon = helpers.getGuiHelper().createDrawableItemLike(ItemRegistry.MYTHICAL_CARD_PACK.get());
    }

    @Override
    public IRecipeType<CardPackDropManager.DropEntry> getRecipeType() {
        return JeiIntegrationPlugin.PACK_DROP;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, CardPackDropManager.DropEntry recipe, IFocusGroup focuses) {
        Component table = Component.translatable(recipe.id().toLanguageKey("pack_drop"));
        List<FormattedText> text = List.of(
                table,
                Component.literal(recipe.id().toString()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
        );
        builder.addText(text, getWidth(), 20)
                .setPosition(0, 0)
                .setColor(0xFF505050)
                .setLineSpacing(0)
                .setTextAlignment(VerticalAlignment.CENTER)
                .setTextAlignment(HorizontalAlignment.CENTER);

        IRecipeSlotDrawablesView recipeSlots = builder.getRecipeSlots();
        List<IRecipeSlotDrawable> outputSlots = recipeSlots.getSlots(RecipeIngredientRole.OUTPUT);
        IScrollGridWidget scrollGridWidget = builder.addScrollGridWidget(outputSlots, 9, 6);
        scrollGridWidget.setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CardPackDropManager.DropEntry recipe, IFocusGroup focuses) {
        Set<Item> uniqueItemSet = new HashSet<>();
        List<ItemStackTemplate> items = recipe.provider().view().toList();
        for (ItemStackTemplate item : items) {
            Item itemType = item.item().value();
            if (uniqueItemSet.add(itemType)) {
                builder.addOutputSlot().add(item.create());
            }
        }
    }

    @Override
    public int getWidth() {
        return 178;
    }

    @Override
    public int getHeight() {
        return 130;
    }
}
