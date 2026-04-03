package team.tnt.collectorsalbum.integrations.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.resource.CardPackDropManager;

@JeiPlugin
public class JeiIntegrationPlugin implements IModPlugin {

    public static final Identifier PLUGIN_ID = Identifier.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "jei");

    public static final IRecipeType<CardPackDropManager.DropEntry> PACK_DROP = IRecipeType.create(CollectorsAlbum.MOD_ID, "pack_drop", CardPackDropManager.DropEntry.class);

    @Override
    public Identifier getPluginUid() {
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
