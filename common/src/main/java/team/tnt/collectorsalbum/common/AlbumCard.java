package team.tnt.collectorsalbum.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface AlbumCard {

    ResourceLocation identifier();

    AlbumCategory category();

    ItemStack asItem();

    int getCardNumber();
}
