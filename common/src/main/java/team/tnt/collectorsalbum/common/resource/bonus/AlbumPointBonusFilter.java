package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumBonusDescriptionOutput;
import team.tnt.collectorsalbum.common.CommonLabels;
import team.tnt.collectorsalbum.common.card.CardCategoryFilter;
import team.tnt.collectorsalbum.common.card.IntFilter;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.Collections;
import java.util.List;

public class AlbumPointBonusFilter implements IntermediateAlbumBonus {

    public static final MapCodec<AlbumPointBonusFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IntFilter.CODEC.fieldOf("filter").forGetter(t -> t.range),
            AlbumBonusType.INSTANCE_CODEC.fieldOf("item").forGetter(t -> t.item)
    ).apply(instance, AlbumPointBonusFilter::new));
    public static final Component POINT_FILTER = Component.translatable("collectorsalbum.label.bonus.point_filter");

    private final IntFilter range;
    private final AlbumBonus item;

    public AlbumPointBonusFilter(IntFilter range, AlbumBonus item) {
        this.range = range;
        this.item = item;
    }

    @Override
    public void addDescription(AlbumBonusDescriptionOutput description) {
        boolean applicable = this.canApply(description.getContext());
        Component base = this.range.getDisplayComponent();
        Component value = base != null ? Component.literal(base.getString() + " - " + CommonLabels.getBoolState(applicable).getString()).withStyle(AlbumBonusDescriptionOutput.getBooleanColor(applicable)) : null;
        description.condition(POINT_FILTER, value, applicable, this);
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
        return this.range.test(album.getPoints());
    }
}
