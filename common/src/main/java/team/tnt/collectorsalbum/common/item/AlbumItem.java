package team.tnt.collectorsalbum.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import team.tnt.collectorsalbum.platform.Platform;

import java.util.List;

public class AlbumItem extends Item {

    public static final Component USAGE = Component.translatable("collectorsalbum.label.press_open").withStyle(ChatFormatting.GRAY);

    public AlbumItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            Platform.INSTANCE.openAlbumUi(itemStack);
        }
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.add(USAGE);
    }
}
