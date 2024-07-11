package team.tnt.collectorsalbum.common;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import team.tnt.collectorsalbum.common.card.AlbumCard;

public interface AlbumCategory {

    ResourceLocation identifier();

    Component getDisplayText();

    AlbumCategoryUiTemplate visualTemplate();

    boolean accepts(AlbumCard card);

    int[] getCardNumbers();

    int getPageOrder();

    AlbumCategoryType<?> getType();
}
