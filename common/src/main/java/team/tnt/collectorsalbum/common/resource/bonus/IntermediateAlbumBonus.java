package team.tnt.collectorsalbum.common.resource.bonus;

import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.List;

public interface IntermediateAlbumBonus extends AlbumBonus {

    boolean canApply(ActionContext context);

    List<AlbumBonus> children();
}
