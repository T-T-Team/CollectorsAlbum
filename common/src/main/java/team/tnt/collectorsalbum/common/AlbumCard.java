package team.tnt.collectorsalbum.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface AlbumCard {

    ResourceLocation identifier();

    AlbumCategoryType<?> category();

    ItemStack asItem();

    int cardNumber();

    AlbumCardType<?> getType();
}
