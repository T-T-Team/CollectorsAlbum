package team.tnt.collectorsalbum.common.resource.bonus;

import team.tnt.collectorsalbum.common.AlbumBonusDescriptionOutput;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

public interface AlbumBonus {

    void addDescription(AlbumBonusDescriptionOutput description);

    void apply(ActionContext context);

    void removed(ActionContext context);

    AlbumBonusType<?> getType();
}
