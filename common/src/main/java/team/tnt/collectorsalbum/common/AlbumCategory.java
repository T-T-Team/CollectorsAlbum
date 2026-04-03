package team.tnt.collectorsalbum.common;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public interface AlbumCategory {

    Identifier identifier();

    Component getDisplayText();

    AlbumCategoryUiTemplate visualTemplate();

    int[] getCardNumbers();

    int getPageOrder();

    AlbumCategoryType<?> getType();
}
