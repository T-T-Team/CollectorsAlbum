package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.MapCodec;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

public class NoBonus implements AlbumBonus {

    public static final NoBonus INSTANCE = new NoBonus();
    public static final MapCodec<NoBonus> CODEC = MapCodec.unit(INSTANCE);

    private NoBonus() {}

    @Override
    public void apply(ActionContext context) {
    }

    @Override
    public void removed(ActionContext context) {
    }

    @Override
    public AlbumBonusType<?> getType() {
        return AlbumBonusRegistry.NONE.get();
    }
}
