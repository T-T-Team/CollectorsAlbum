package team.tnt.collectorsalbum.mixin;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {

    @Inject(
            method = "getTooltipLines",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/world/item/ItemStack;addDetailsToTooltip(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;Ljava/util/function/Consumer;)V"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void collectorsAlbum$getTooltipLines(Item.TooltipContext context, @Nullable Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, TooltipDisplay display, List<Component> lines) {
        ItemStack itemStack = (ItemStack) (Object) this;
        Item item = itemStack.getItem();
        AlbumCardManager manager = AlbumCardManager.getInstance();
        manager.getCardInfo(item).ifPresent(info -> info.appendItemStackHoverTooltip(itemStack, context, lines, tooltipFlag));
        if (itemStack.has(ItemDataComponentRegistry.PACK_DROPS_TABLE.get())) {
            CollectorsAlbum.addCardPackTooltip(itemStack, context, lines, tooltipFlag);
        }
    }

    @Inject(
            method = "getUseAnimation",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void collectorsAlbum$getUseAnimationForPack(CallbackInfoReturnable<ItemUseAnimation> cir) {
        ItemStack itemStack = (ItemStack) (Object) this;
        if (itemStack.has(ItemDataComponentRegistry.PACK_DROPS_TABLE.get())) {
            cir.setReturnValue(ItemUseAnimation.BOW);
        }
    }
}
