package team.tnt.collectorsalbum.platform.network;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import team.tnt.collectorsalbum.platform.Side;

import java.util.List;

public final class ForgeNetwork implements Network {

    private SimpleChannel channel;
    private int messageId;

    @Override
    public void initialize(ResourceLocation identifier, Side side, List<PacketHolder<?, ?>> c2s, List<PacketHolder<?, ?>> s2c) {
        this.channel = NetworkRegistry.ChannelBuilder.named(identifier)
                .networkProtocolVersion(() -> "1")
                .clientAcceptedVersions(s -> s.equals("1"))
                .serverAcceptedVersions(s -> s.equals("1"))
                .simpleChannel();

        for (PacketHolder<?, ?> holder : c2s) {
            this.registerInternal(holder);
        }
        for (PacketHolder<?, ?> holder : s2c) {
            this.registerInternal(holder);
        }
    }

    @Override
    public void sendServerMessage(NetworkMessage payload) {
        channel.sendToServer(payload);
    }

    @Override
    public void sendClientMessage(ServerPlayer player, NetworkMessage payload) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), payload);
    }

    private <T extends NetworkMessage> void registerInternal(PacketHolder<T, ?> holder) {
        this.channel.messageBuilder(holder.payloadType(), messageId++)
                .encoder(NetworkMessage::write)
                .decoder(holder.decoder())
                .consumerMainThread((t, contextSupplier) -> {
                    Player player = this.getPlayer(contextSupplier.get());
                    t.handle(player);
                })
                .add();
    }

    private Player getPlayer(NetworkEvent.Context ctx) {
        if (ctx.getDirection().getReceptionSide().isServer()) {
            return ctx.getSender();
        } else {
            return getLocalPlayer();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private Player getLocalPlayer() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player;
    }
}
