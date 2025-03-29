package team.tnt.collectorsalbum.mixin;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;

@Mixin(ItemStack.class)
public abstract class FabricItemStackMixin implements DataComponentHolder {

    @Inject(
            method = "getUseDuration",
            at = @At("HEAD"),
            cancellable = true
    )
    private void collectorsalbum$getUseDuration(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        ItemStack itemstack = (ItemStack)(Object)this;
        if (itemstack.has(ItemDataComponentRegistry.PACK_DROPS_TABLE.get())) {
            cir.setReturnValue(20);
        }
    }

    @Inject(
            method = "finishUsingItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void collectorsalbum$finishUsingItem(Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack itemstack = (ItemStack)(Object)this;
        if (itemstack.has(ItemDataComponentRegistry.PACK_DROPS_TABLE.get()) && livingEntity instanceof ServerPlayer player) {
            CollectorsAlbum.openPack(player);
            cir.setReturnValue(itemstack);
        }
    }
}
