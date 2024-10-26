package team.tnt.collectorsalbum.platform.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import team.tnt.collectorsalbum.platform.Side;

import java.util.List;

public class FabricNetwork implements Network {

    @Override
    public void initialize(ResourceLocation identifier, Side side, List<PacketHolder<?, ?>> c2s, List<PacketHolder<?, ?>> s2c) {
        if (side == Side.CLIENT) {
            // client packets
            s2c.forEach(this::registerClientPacket);
        } else {
            // server packets
            c2s.forEach(this::registerServerPacket);
        }
    }

    @Override
    public void sendClientMessage(ServerPlayer player, NetworkMessage payload) {
        ServerPlayNetworking.send(player, payload.getPacketId(), createPayload(payload));
    }

    @Override
    public void sendServerMessage(NetworkMessage payload) {
        ClientPlayNetworking.send(payload.getPacketId(), createPayload(payload));
    }

    @Environment(EnvType.CLIENT)
    private <T extends NetworkMessage> void registerClientPacket(PacketHolder<T, ?> holder) {
        ClientPlayNetworking.registerGlobalReceiver(holder.pid(), (client, handler, buf, responseSender) -> {
            T message = holder.decoder().apply(buf);
            registerClientPayloadHandler(client, message);
        });
    }

    @Environment(EnvType.CLIENT)
    private void registerClientPayloadHandler(Minecraft client, NetworkMessage message) {
        client.execute(new Runnable() { // server won't load without this...
            @Override
            @Environment(EnvType.CLIENT)
            public void run() {
                message.handle(client.player);
            }
        });
    }

    private static FriendlyByteBuf createPayload(NetworkMessage message) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        message.write(buffer);
        return buffer;
    }

    private <T extends NetworkMessage> void registerServerPacket(PacketHolder<T, ?> holder) {
        ServerPlayNetworking.registerGlobalReceiver(holder.pid(), (server, player, handler, buf, responseSender) -> {
            T message = holder.decoder().apply(buf);
            server.execute(() -> message.handle(player));
        });
    }
}
