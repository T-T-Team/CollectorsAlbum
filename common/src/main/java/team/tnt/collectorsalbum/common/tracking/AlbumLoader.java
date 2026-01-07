package team.tnt.collectorsalbum.common.tracking;

import net.minecraft.world.entity.player.Player;

import java.util.function.IntFunction;

@FunctionalInterface
public interface AlbumLoader {
    CachedAlbum find(Player player, IntFunction<InventoryKey> keyFactory);
}
