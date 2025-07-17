package team.tnt.collectorsalbum.integrations.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.resource.CardPackDropManager;

@JeiPlugin
public class JeiIntegrationPlugin implements IModPlugin {

    public static final ResourceLocation PLUGIN_ID = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "jei");

    public static final RecipeType<CardPackDropManager.DropEntry> PACK_DROP = RecipeType.create(CollectorsAlbum.MOD_ID, "pack_drop", CardPackDropManager.DropEntry.class);

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CardPackDropRecipeCategory(registration.getJeiHelpers()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(PACK_DROP, CardPackDropManager.getInstance().getDataForSync());
    }
}
