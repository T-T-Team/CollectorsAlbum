package team.tnt.collectorsalbum.common;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public interface AlbumCategory {

    ResourceLocation identifier();

    Component getDisplayText();

    AlbumCategoryUiTemplate visualTemplate();

    int[] getCardNumbers();

    int getPageOrder();

    AlbumCategoryType<?> getType();

    default int getCardCount() {
        return this.getCardNumbers().length;
    }

    default int getPageSize() {
        int rows = visualTemplate().slotTemplate.rows();
        int columns = visualTemplate().slotTemplate.columns();
        return 2 * rows * columns;
    }

    default int getPageCount() {
        int cardCount = this.getCardCount();
        int pageSize = this.getPageSize();
        return Mth.ceil(cardCount / (float) pageSize);
    }
}
