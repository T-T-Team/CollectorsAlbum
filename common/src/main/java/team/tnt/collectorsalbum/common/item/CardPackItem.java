package team.tnt.collectorsalbum.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.CardPackDropManager;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropProvider;
import team.tnt.collectorsalbum.common.resource.util.ListBasedOutputBuilder;
import team.tnt.collectorsalbum.network.S2C_OpenCardPackScreen;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CardPackItem extends Item {

    public static final int MAX_PACK_CARDS = 18;
    public static final Component USAGE = Component.translatable("collectorsalbum.label.use_open").withStyle(ChatFormatting.GRAY);

    private final ResourceLocation lootDataSourcePath;

    public CardPackItem(Properties properties, ResourceLocation lootDataSourcePath) {
        super(properties);
        this.lootDataSourcePath = lootDataSourcePath;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity entity) {
        return 20;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer player && !level.isClientSide()) {
            ListBasedOutputBuilder<ItemStack> outputs = ListBasedOutputBuilder.createArrayListBased();
            ItemDropProvider provider = CardPackDropManager.getInstance().getProvider(this.lootDataSourcePath);
            ActionContext context = ActionContext.of(ActionContext.PLAYER, player, ActionContext.ITEMSTACK, itemStack, ActionContext.RANDOM, player.getRandom());
            provider.generateDrops(context, outputs);
            AlbumCardManager cardManager = AlbumCardManager.getInstance();
            List<ItemStack> validDrops = outputs.getItems().stream()
                    .filter(stack -> cardManager.isCard(stack.getItem()))
                    .collect(Collectors.toList());
            Collections.shuffle(validDrops);
            itemStack.set(ItemDataComponentRegistry.PACK_DROPS.get(), validDrops); // TODO remove
            if (!player.isCreative())
                itemStack.shrink(1);
            // TODO save serverside data only and then award items from that
            PlatformNetworkManager.NETWORK.sendClientMessage(player, new S2C_OpenCardPackScreen(validDrops));
        }
        return itemStack;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        components.add(USAGE);
    }
}
