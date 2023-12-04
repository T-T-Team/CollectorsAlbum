package team.tnt.collectoralbum.common.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.common.init.SoundRegistry;
import team.tnt.collectoralbum.data.packs.ICardDropProvider;
import team.tnt.collectoralbum.network.Networking;
import team.tnt.collectoralbum.network.packet.OpenCardScreenPacket;
import team.tnt.collectoralbum.server.OpenCardPackContextHolder;

import java.util.List;
import java.util.Optional;

public class CardPackItem extends Item {

    private final ResourceLocation dropsProviderPath;

    public CardPackItem(ResourceLocation dropsProviderPath) {
        super(new Properties());
        this.dropsProviderPath = dropsProviderPath;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 20;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player) {
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            Optional<ICardDropProvider> optional = CollectorsAlbum.CARD_PACK_MANAGER.getProvider(dropsProviderPath);
            optional.ifPresent(provider -> {
                List<ItemStack> itemStacks = provider.provideDrops();
                OpenCardPackContextHolder.store(player, itemStacks);
                Networking.dispatchClientPacket(player, new OpenCardScreenPacket(itemStacks));
            });
            ItemCooldowns cooldowns = player.getCooldowns();
            cooldowns.addCooldown(stack.getItem(), 10);
        } else {
            livingEntity.playSound(SoundRegistry.OPEN.get(), 0.8f, 1.0f);
        }
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        ItemCooldowns cooldowns = player.getCooldowns();
        if (cooldowns.isOnCooldown(stack.getItem())) {
            return InteractionResultHolder.pass(stack);
        }
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(stack);
    }
}
