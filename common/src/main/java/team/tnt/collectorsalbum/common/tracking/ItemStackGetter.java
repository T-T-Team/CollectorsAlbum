package team.tnt.collectorsalbum.common.tracking;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ItemStackGetter {
    ItemStack get(Player player, int slot);
}
