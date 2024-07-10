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
import team.tnt.collectorsalbum.platform.PlatformPlayerHelper;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;

import java.util.List;

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
        List<ItemStack> drops = itemStack.get(ItemDataComponentRegistry.PACK_DROPS.get());
        if (drops != null && !drops.isEmpty()) {
            for (ItemStack item : drops) {
                PlatformPlayerHelper.giveItemStackOrDrop(player, item.copy());
            }
            itemStack.remove(ItemDataComponentRegistry.PACK_DROPS.get());
            if (!player.isCreative())
                itemStack.shrink(1);
        } else {
            CollectorsAlbum.LOGGER.warn("Could not find generated card pack items on item {}", itemStack);
        }
    }
}
