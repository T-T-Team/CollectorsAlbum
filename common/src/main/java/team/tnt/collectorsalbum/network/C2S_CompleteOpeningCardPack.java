package team.tnt.collectorsalbum.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.item.CardPackItem;
import team.tnt.collectorsalbum.platform.PlatformPlayerHelper;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;

public record C2S_CompleteOpeningCardPack() implements CustomPacketPayload {

    public static final ResourceLocation IDENTIFIER = PlatformNetworkManager.generatePacketIdentifier(CollectorsAlbum.MOD_ID, C2S_CompleteOpeningCardPack.class);
    public static final Type<C2S_CompleteOpeningCardPack> TYPE = new Type<>(IDENTIFIER);
    public static final StreamCodec<FriendlyByteBuf, C2S_CompleteOpeningCardPack> CODEC = StreamCodec.of(
            (buffer, payload) -> {},
            buffer -> new C2S_CompleteOpeningCardPack()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void onPacketReceived(Player player) {
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        CardPackItem.PackContents contents = itemStack.get(ItemDataComponentRegistry.PACK_CONTENTS.get());
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
}
