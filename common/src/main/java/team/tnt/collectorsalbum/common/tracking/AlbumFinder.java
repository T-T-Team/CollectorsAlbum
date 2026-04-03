package team.tnt.collectorsalbum.common.tracking;

import net.minecraft.resources.Identifier;

public record AlbumFinder(Identifier id, AlbumLoader loader,
                          ItemStackGetter itemGetter) {
}
