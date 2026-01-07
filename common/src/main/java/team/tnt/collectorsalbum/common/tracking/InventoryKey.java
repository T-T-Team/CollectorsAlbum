package team.tnt.collectorsalbum.common.tracking;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record InventoryKey(AlbumFinder finder, int slot) {

    public ItemStack getItem(Player player) {
        return finder.itemGetter().get(player, slot);
    }
}
