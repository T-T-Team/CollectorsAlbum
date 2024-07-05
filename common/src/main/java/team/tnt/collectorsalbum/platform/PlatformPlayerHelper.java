package team.tnt.collectorsalbum.platform;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class PlatformPlayerHelper {

    public static void giveItemStackOrDrop(Player player, ItemStack itemStack) {
        if (player.level().isClientSide())
            return;
        if (!player.getInventory().add(itemStack)) {
            ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), itemStack);
            itemEntity.setDefaultPickUpDelay();
            player.level().addFreshEntity(itemEntity);
        }
    }
}
