package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.List;

public class FirstApplicableBonus implements IntermediateAlbumBonus {

    public static final MapCodec<FirstApplicableBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AlbumBonusType.INSTANCE_CODEC.listOf().fieldOf("items").forGetter(t -> t.items)
    ).apply(instance, FirstApplicableBonus::new));

    private final List<AlbumBonus> items;

    public FirstApplicableBonus(List<AlbumBonus> items) {
        this.items = items;
    }

    @Override
    public boolean canApply(ActionContext context) {
        for (AlbumBonus item : items) {
            if (!(item instanceof IntermediateAlbumBonus intermediateAlbumBonus) || intermediateAlbumBonus.canApply(context)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply(ActionContext context) {
        for (AlbumBonus bonus : items) {
            if (!(bonus instanceof IntermediateAlbumBonus intermediateAlbumBonus) || intermediateAlbumBonus.canApply(context)) {
                bonus.apply(context);
                break;
            } else {
                bonus.removed(context);
            }
        }
    }

    @Override
    public void removed(ActionContext context) {
        items.forEach(item -> item.removed(context));
    }

    @Override
    public List<AlbumBonus> children() {
        return this.items;
    }

    @Override
    public AlbumBonusType<?> getType() {
        return AlbumBonusRegistry.FIRST_APPLICABLE.get();
    }
}
