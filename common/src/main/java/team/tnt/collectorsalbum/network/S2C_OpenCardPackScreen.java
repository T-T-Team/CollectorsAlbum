package team.tnt.collectorsalbum.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.client.CollectorsAlbumClient;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;

import java.util.List;

public record S2C_OpenCardPackScreen(List<ItemStack> items) implements CustomPacketPayload {

    public static final ResourceLocation IDENTIFIER = PlatformNetworkManager.generatePacketIdentifier(CollectorsAlbum.MOD_ID, S2C_OpenCardPackScreen.class);
    public static final Type<S2C_OpenCardPackScreen> TYPE = new Type<>(IDENTIFIER);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2C_OpenCardPackScreen> CODEC = StreamCodec.composite(
            ItemStack.LIST_STREAM_CODEC, S2C_OpenCardPackScreen::items,
            S2C_OpenCardPackScreen::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void onPacketReceived(Player player) {
        CollectorsAlbumClient.handlePackOpening(items());
    }
}
