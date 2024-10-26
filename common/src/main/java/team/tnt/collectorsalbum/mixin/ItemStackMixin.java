package team.tnt.collectorsalbum.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(
            method = "getTooltipLines",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void collectorsAlbum$getTooltipLines(Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir, List<Component> components) {
        ItemStack itemStack = (ItemStack) (Object) this;
        Item item = itemStack.getItem();
        AlbumCardManager manager = AlbumCardManager.getInstance();
        manager.getCardInfo(item).ifPresent(info -> info.appendItemStackHoverTooltip(itemStack, components, flag));
    }
}
