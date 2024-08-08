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
import team.tnt.collectorsalbum.common.init.SoundRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.CardPackDropManager;
import team.tnt.collectorsalbum.common.resource.drops.NoItemDropProvider;
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
    public static final Component LABEL_UNSET = Component.translatable("collectorsalbum.label.not_set").withStyle(ChatFormatting.RED);
    private static final Component WARN_NO_DROPS = Component.translatable("collectorsalbum.label.pack_empty").withStyle(ChatFormatting.GOLD);

    private final ResourceLocation lootDataSourcePath;

    public CardPackItem(Properties properties) {
        this(properties, null);
    }

    public CardPackItem(Properties properties, ResourceLocation lootDataSourcePath) {
        super(properties);
        this.lootDataSourcePath = lootDataSourcePath;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        player.playSound(SoundRegistry.PACK_OPEN.get(), 1.0F, 1.0F);
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
            ItemDropProvider provider = this.getDropTable(itemStack);
            ActionContext context = ActionContext.of(ActionContext.PLAYER, player, ActionContext.ITEMSTACK, itemStack, ActionContext.RANDOM, player.getRandom());
            provider.generateDrops(context, outputs);
            AlbumCardManager cardManager = AlbumCardManager.getInstance();
            List<ItemStack> validDrops = outputs.getItems().stream()
                    .filter(stack -> cardManager.isCard(stack.getItem()))
                    .collect(Collectors.toList());
            if (!validDrops.isEmpty()) {
                Collections.shuffle(validDrops);
                itemStack.set(ItemDataComponentRegistry.PACK_DROPS.get(), validDrops);
                PlatformNetworkManager.NETWORK.sendClientMessage(player, new S2C_OpenCardPackScreen(validDrops));
            } else {
                player.displayClientMessage(WARN_NO_DROPS, true);
            }
            player.getCooldowns().addCooldown(this, 20);
        }
        return itemStack;
    }

    protected ItemDropProvider getDropTable(ItemStack itemStack) {
        CardPackDropManager manager = CardPackDropManager.getInstance();
        ResourceLocation customPath = itemStack.get(ItemDataComponentRegistry.PACK_DROPS_TABLE.get());
        if (customPath != null) {
            return manager.getEitherProvider(customPath, this.lootDataSourcePath);
        } else if (this.lootDataSourcePath != null) {
            return manager.getProvider(this.lootDataSourcePath);
        }
        return NoItemDropProvider.INSTANCE;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        components.add(USAGE);
        ResourceLocation customTable = itemStack.get(ItemDataComponentRegistry.PACK_DROPS_TABLE.get());
        if (customTable != null) {
            Component customTableLabel = Component.literal(customTable.toString()).withStyle(ChatFormatting.GREEN);
            components.add(Component.translatable("collectorsalbum.label.custom_drop_table", customTableLabel).withStyle(ChatFormatting.GRAY));
        } else if (this.lootDataSourcePath == null) {
            components.add(Component.translatable("collectorsalbum.label.custom_drop_table", LABEL_UNSET).withStyle(ChatFormatting.GRAY));
        }
    }
}
