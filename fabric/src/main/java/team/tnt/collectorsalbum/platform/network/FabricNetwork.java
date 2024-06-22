package team.tnt.collectorsalbum.platform.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class FabricNetwork implements Network {

    @Override
    public void initialize(ResourceLocation identifier, List<PacketHolder<?>> c2s, List<PacketHolder<?>> s2c) {
        PayloadTypeRegistry<RegistryFriendlyByteBuf> c2sRegistrar = PayloadTypeRegistry.playC2S();
        PayloadTypeRegistry<RegistryFriendlyByteBuf> s2cRegistrar = PayloadTypeRegistry.playS2C();

        c2s.forEach(holder -> registerInternal(c2sRegistrar, holder, PacketDirection.CLIENT_TO_SERVER));
        s2c.forEach(holder -> registerInternal(s2cRegistrar, holder, PacketDirection.SERVER_TO_CLIENT));
    }

    @Override
    public void sendClientMessage(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void sendServerMessage(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    private <P extends CustomPacketPayload> void registerInternal(PayloadTypeRegistry<?> registrar, PacketHolder<P> holder, PacketDirection direction) {
        registrar.register(holder.type(), holder.codec());

        PacketHandler<P> handler = holder.handler();
        if (handler != null) {
            switch (direction) {
                case CLIENT_TO_SERVER -> ServerPlayNetworking.registerGlobalReceiver(holder.type(), (payload, context) -> handler.handle(payload, context.player()));
                case SERVER_TO_CLIENT -> ClientPlayNetworking.registerGlobalReceiver(holder.type(), (payload, context) -> handleClientSide(payload, context, handler));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private <P extends CustomPacketPayload> void handleClientSide(P payload, ClientPlayNetworking.Context context, PacketHandler<P> handler) {
        handler.handle(payload, context.player());
    }
}
