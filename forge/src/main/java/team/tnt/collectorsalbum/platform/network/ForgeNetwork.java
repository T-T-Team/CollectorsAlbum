package team.tnt.collectorsalbum.platform.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.util.List;

public final class ForgeNetwork implements Network {

    private SimpleChannel channel;

    @Override
    public void initialize(ResourceLocation identifier, List<PacketHolder<?>> c2s, List<PacketHolder<?>> s2c) {
        this.channel = ChannelBuilder.named(identifier)
                .networkProtocolVersion(1)
                .simpleChannel();

        for (PacketHolder<?> holder : c2s) {
            this.registerInternal(holder);
        }
        for (PacketHolder<?> holder : s2c) {
            this.registerInternal(holder);
        }
    }

    @Override
    public void sendServerMessage(CustomPacketPayload payload) {
        channel.send(payload, PacketDistributor.SERVER.noArg());
    }

    @Override
    public void sendClientMessage(ServerPlayer player, CustomPacketPayload payload) {
        channel.send(payload, PacketDistributor.PLAYER.with(player));
    }

    private <T extends CustomPacketPayload> void registerInternal(PacketHolder<T> holder) {
        this.channel.messageBuilder(holder.payloadType())
                .codec(holder.codec())
                .consumerMainThread((payload, ctx) -> {
                    if (holder.handler() != null) {
                        holder.handler().handle(payload, this.getPlayer(ctx));
                    }
                })
                .add();
    }

    private Player getPlayer(CustomPayloadEvent.Context ctx) {
        if (ctx.isServerSide()) {
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
