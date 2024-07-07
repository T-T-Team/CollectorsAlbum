package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.function.ConstantNumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProviderType;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class AlbumPointBonusFilter implements IntermediateAlbumBonus {

    public static final MapCodec<AlbumPointBonusFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(Codec.INT, NumberProviderType.INSTANCE_CODEC).optionalFieldOf("min", Either.left(Integer.MIN_VALUE)).forGetter(t -> Either.right(t.min)),
            Codec.either(Codec.INT, NumberProviderType.INSTANCE_CODEC).optionalFieldOf("max", Either.left(Integer.MAX_VALUE)).forGetter(t -> Either.right(t.max)),
            AlbumBonusType.INSTANCE_CODEC.fieldOf("item").forGetter(t -> t.item)
    ).apply(instance, AlbumPointBonusFilter::new));

    private final NumberProvider min;
    private final NumberProvider max;
    private final AlbumBonus item;

    public AlbumPointBonusFilter(Either<Integer, NumberProvider> min, Either<Integer, NumberProvider> max, AlbumBonus item) {
        this.min = min.map(ConstantNumberProvider::new, Function.identity());
        this.max = min.map(ConstantNumberProvider::new, Function.identity());
        this.item = item;
    }

    @Override
    public void apply(ActionContext context) {
        if (this.canApply(context)) {
            this.item.apply(context);
        } else {
            this.item.removed(context);
        }
    }

    @Override
    public void removed(ActionContext context) {
        item.removed(context);
    }

    @Override
    public List<AlbumBonus> children() {
        return Collections.singletonList(this.item);
    }

    @Override
    public AlbumBonusType<?> getType() {
        return AlbumBonusRegistry.ALBUM_POINT_FILTER.get();
    }

    @Override
    public boolean canApply(ActionContext context) {
        Album album = context.getNullable(ActionContext.ALBUM, Album.class);
        if (album == null) {
            return false;
        }
        int from = min.getAsInt();
        int to = max.getAsInt();
        int points = album.getPoints();
        return points >= from && points <= to;
    }
}
