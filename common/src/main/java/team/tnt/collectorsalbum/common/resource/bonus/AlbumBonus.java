package team.tnt.collectorsalbum.common.resource.bonus;

import team.tnt.collectorsalbum.common.resource.util.ActionContext;

public interface AlbumBonus {

    void apply(ActionContext context);

    void removed(ActionContext context);

    AlbumBonusType<?> getType();
}
