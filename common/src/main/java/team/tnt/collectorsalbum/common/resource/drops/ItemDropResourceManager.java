package team.tnt.collectorsalbum.common.resource.drops;

import net.minecraft.resources.ResourceLocation;

public interface ItemDropResourceManager {

    ItemDropProvider getProvider(ResourceLocation path);
}
