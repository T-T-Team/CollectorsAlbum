package team.tnt.collectorsalbum.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.tnt.collectorsalbum.common.resource.MobAdditionalDropManager;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropProvider;
import team.tnt.collectorsalbum.common.resource.util.SingleItemOutputBuilder;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {

    public LivingEntityMixin(EntityType<?> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Inject(method = "die", at = @At("RETURN"))
    private void collectorsAlbum$die(DamageSource source, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        Entity killer = source.getEntity();
        GameRules gameRules = level().getGameRules();
        if (!gameRules.getBoolean(GameRules.RULE_DOMOBLOOT))
            return;
        if (!livingEntity.level().isClientSide() && killer instanceof Player player) {
            ActionContext context = ActionContext.of(
                    ActionContext.ENTITY, livingEntity,
                    ActionContext.PLAYER, player,
                    ActionContext.ITEMSTACK, player.getMainHandItem(),
                    ActionContext.DAMAGE_SOURCE, source,
                    ActionContext.RANDOM, livingEntity.getRandom()
            );
            SingleItemOutputBuilder<ItemStack> builder = SingleItemOutputBuilder.acceptsFirst();
            MobAdditionalDropManager dropManager = MobAdditionalDropManager.getInstance();
            for (ItemDropProvider provider : dropManager) {
                provider.generateDrops(context, builder);
                if (builder.isLocked())
                    break;
            }
            ItemStack drop = builder.getItemOrDefault(ItemStack.EMPTY);
            if (!drop.isEmpty()) {
                ItemEntity entity = new ItemEntity(level(), getX(), getY(), getZ(), drop);
                entity.setDefaultPickUpDelay();
                livingEntity.level().addFreshEntity(entity);
            }
        }
    }
}
