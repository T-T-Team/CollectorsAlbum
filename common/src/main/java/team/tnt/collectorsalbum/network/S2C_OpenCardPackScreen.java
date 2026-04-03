package team.tnt.collectorsalbum.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.client.CollectorsAlbumClient;
import team.tnt.collectorsalbum.common.item.PackContents;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;

import java.util.List;

public record S2C_OpenCardPackScreen(PackContents contents) implements CustomPacketPayload {

    public static final Identifier IDENTIFIER = PlatformNetworkManager.generatePacketIdentifier(CollectorsAlbum.MOD_ID, S2C_OpenCardPackScreen.class);
    public static final Type<S2C_OpenCardPackScreen> TYPE = new Type<>(IDENTIFIER);
    public static final StreamCodec<RegistryFriendlyByteBuf, S2C_OpenCardPackScreen> CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), pkt -> pkt.contents().drops(),
            S2C_OpenCardPackScreen::new
    );

    public S2C_OpenCardPackScreen(List<ItemStack> drops) {
        this(new PackContents(drops));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void onPacketReceived(Player player) {
        CollectorsAlbumClient.handlePackOpening(contents().drops());
    }
}
