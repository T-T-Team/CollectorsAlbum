package team.tnt.collectorsalbum.common.tracking;

import net.minecraft.resources.ResourceLocation;

public record AlbumFinder(ResourceLocation id, AlbumLoader loader,
                          ItemStackGetter itemGetter) {
}
