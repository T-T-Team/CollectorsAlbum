package team.tnt.collectorsalbum.common;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface AlbumCategory {

    ResourceLocation identifier();

    Component getDisplayText();

    AlbumCategoryUiTemplate visualTemplate();

    boolean accepts(AlbumCard card);

    int[] getCardNumbers();

    int getPageOrder();

    AlbumCategoryType<?> getType();
}
