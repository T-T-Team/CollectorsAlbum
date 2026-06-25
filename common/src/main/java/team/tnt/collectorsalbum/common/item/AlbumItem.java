package team.tnt.collectorsalbum.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import team.tnt.collectorsalbum.client.CollectorsAlbumClient;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.tracking.PlayerAlbumTracker;
import team.tnt.collectorsalbum.platform.Platform;
import team.tnt.collectorsalbum.platform.Side;

import java.util.UUID;
import java.util.function.Consumer;

public class AlbumItem extends Item {

    public static final Component USAGE = Component.translatable("collectorsalbum.label.press_open").withStyle(ChatFormatting.GRAY);
    public static final Component ALBUM_ACTIVE = Component.translatable("collectorsalbum.label.album_active").withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC);
    public static final Component ALBUM_INACTIVE = Component.translatable("collectorsalbum.label.album_inactive").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC);

    public AlbumItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            CollectorsAlbumClient.openAlbum(itemStack);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        builder.accept(USAGE);
        Album album = Album.fromItem(itemStack);
        if (album == null)
            return;
        PlayerAlbumTracker tracker = PlayerAlbumTracker.get();
        if (Platform.INSTANCE.getSide() == Side.CLIENT) {
            UUID clientId = CollectorsAlbumClient.getClientUUID();
            if (clientId == null)
                return;
            Component label = tracker.matches(clientId, album)
                    ? ALBUM_ACTIVE
                    : ALBUM_INACTIVE;
            builder.accept(label);
        }
    }
}
