package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.common.CollectorsAlbumRegistries;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.function.Function;

public interface AlbumBonus {

    Codec<AlbumBonus> CODEC = CollectorsAlbumRegistries.ALBUM_BONUS.byNameCodec().dispatch(AlbumBonus::codec, Function.identity());

    void apply(ActionContext context);

    void removed(ActionContext context);

    default void appendDetails(SectionOutput writer, ActionContext ctx) {}

    MapCodec<? extends AlbumBonus> codec();
}
