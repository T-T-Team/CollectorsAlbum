package team.tnt.collectorsalbum.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.item.CardPackItem;
import team.tnt.collectorsalbum.platform.PlatformPlayerHelper;
import team.tnt.collectorsalbum.platform.network.NetworkMessage;

public record C2S_CompleteOpeningCardPack() implements NetworkMessage {

    public static final ResourceLocation IDENTIFIER = new ResourceLocation(CollectorsAlbum.MOD_ID, "msg_complete_card_pack_opening");

    @Override
    public ResourceLocation getPacketId() {
        return IDENTIFIER;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(Player player) {
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        CardPackItem.PackContents contents = CardPackItem.PackContents.get(itemStack);
        CollectorsAlbum.LOGGER.debug("{} has requested pack content drops. Received content list from item {}: {}", player, itemStack, contents);
        if (contents != null && !contents.isEmpty()) {
            for (ItemStack item : contents.contents()) {
                PlatformPlayerHelper.giveItemStackOrDrop(player, item.copy());
            }
            if (!player.isCreative())
                itemStack.shrink(1);
        } else {
            CollectorsAlbum.LOGGER.warn("Could not find generated card pack items on item {}", itemStack);
        }
    }

    public static C2S_CompleteOpeningCardPack read(FriendlyByteBuf buf) {
        return new C2S_CompleteOpeningCardPack();
    }
}
