package team.tnt.collectorsalbum.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.init.RegistryTags;

public class AlbumRestrictedSlot extends Slot {

    public AlbumRestrictedSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    @Override
    public boolean mayPickup(Player player) {
        ItemStack itemStack = this.getItem();
        return !itemStack.is(RegistryTags.Items.ALBUM);
    }
}
