package team.tnt.collectorsalbum.common.resource.drops;

import net.minecraft.resources.Identifier;

public interface ItemDropResourceManager {

    ItemDropProvider getProvider(Identifier path);
}
