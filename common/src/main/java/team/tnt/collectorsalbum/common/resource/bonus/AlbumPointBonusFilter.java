package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.card.IntFilter;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.Collections;
import java.util.List;

public record AlbumPointBonusFilter(IntFilter range, AlbumBonus item) implements IntermediateAlbumBonus {

    public static final MapCodec<AlbumPointBonusFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IntFilter.CODEC.fieldOf("filter").forGetter(t -> t.range),
            AlbumBonusType.INSTANCE_CODEC.fieldOf("item").forGetter(t -> t.item)
    ).apply(instance, AlbumPointBonusFilter::new));

    @Override
    public void appendDetails(SectionOutput writer, ActionContext ctx) {
        int points = this.getPoints(ctx);
        boolean applicable = this.canApply(points);
        Component range = this.range.getDisplayComponent();
        writer.condition(applicable, Component.translatable("collectorsalbum.label.bonus.album_point_filter", range, points));
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
        int points = this.getPoints(context);
        return this.canApply(points);
    }

    private boolean canApply(int points) {
        return this.range.test(points);
    }

    private int getPoints(ActionContext context) {
        Album album = context.getNullable(ActionContext.ALBUM, Album.class);
        return album == null ? 0 : album.getPoints();
    }
}
