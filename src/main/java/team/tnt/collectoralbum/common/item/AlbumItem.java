package team.tnt.collectoralbum.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.client.CollectorsAlbumClient;
import team.tnt.collectoralbum.common.container.AlbumContainer;
import team.tnt.collectoralbum.common.menu.AlbumMenu;

import java.util.Arrays;
import java.util.List;

public class AlbumItem extends Item implements IDeathPersistableItem {

    private static final Component SHOW_BOOSTS = Component.translatable("text.collectorsalbum.album.boost.show").withStyle(ChatFormatting.GRAY);
    private static final String PAGE_INFO_TRANSLATION_KEY = "text.collectorsalbum.album.boost.paging";

    public AlbumItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean shouldKeepItem(Player player, ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            serverPlayer.openMenu(new SimpleMenuProvider((id, inv, owner) -> new AlbumMenu(new AlbumContainer(itemStack), inv, id), CommonComponents.EMPTY), buffer -> {
                buffer.writeItem(itemStack);
                buffer.writeInt(0);
            });
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        if (Screen.hasControlDown()) {
            CollectorsAlbumClient client = CollectorsAlbumClient.getClient();
            int pageIndex = client.getAlbumPageIndex();
            int pageSize = client.getPageCount();
            Component[] pagedText = CollectorsAlbum.ALBUM_CARD_BOOST_MANAGER.getBoosts()
                    .map(coll -> coll.getPagedDescription(pageIndex))
                    .orElse(new Component[0]);
            if (pagedText == null) return;
            tooltipComponents.addAll(Arrays.asList(pagedText));
            tooltipComponents.add(Component.translatable(PAGE_INFO_TRANSLATION_KEY, pageIndex + 1, pageSize).withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltipComponents.add(SHOW_BOOSTS);
        }
    }
}
