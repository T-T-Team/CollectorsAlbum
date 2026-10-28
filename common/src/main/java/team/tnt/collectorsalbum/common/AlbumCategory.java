package team.tnt.collectorsalbum.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import team.tnt.collectorsalbum.CollectorsAlbum;

import java.util.function.Function;

public interface AlbumCategory {

    Codec<AlbumCategory> CODEC = CollectorsAlbum.CATEGORY.byNameCodec().dispatch(AlbumCategory::codec, Function.identity());

    Identifier identifier();

    Component getDisplayText();

    AlbumCategoryUiTemplate visualTemplate();

    int[] getCardNumbers();

    int getPageOrder();

    MapCodec<? extends AlbumCategory> codec();

    default int getCardCount() {
        return this.getCardNumbers().length;
    }

    default int getPageSize() {
        int rows = visualTemplate().slotTemplate().rows();
        int columns = visualTemplate().slotTemplate().columns();
        return 2 * rows * columns;
    }

    default int getPageCount() {
        int cardCount = this.getCardCount();
        int pageSize = this.getPageSize();
        return Mth.ceil(cardCount / (float) pageSize);
    }
}
