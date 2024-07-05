package team.tnt.collectorsalbum.platform.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

public class NeoforgeNetwork implements Network {

    public static void subscribeRegistryEvent(IEventBus eventBus, PlatformNetworkManager manager) {
        eventBus.addListener(EventPriority.NORMAL, false, RegisterPayloadHandlersEvent.class, event -> {
            PayloadRegistrar registrar = event.registrar(manager.identifier().getNamespace());

            manager.bindRef(
                    c2s -> c2s.forEach(holder -> registerInternalC2S(registrar, holder)),
                    s2c -> s2c.forEach(holder -> registerInternalS2C(registrar, holder))
            );
        });
    }

    @Override
    public void initialize(ResourceLocation identifier, List<PacketHolder<?, ?>> c2s, List<PacketHolder<?, ?>> s2c) {
        throw new UnsupportedOperationException("Cannot automatically bind network packets on NeoForge platform. Use specific events to register your packets instead!");
    }

    @Override
    public void sendClientMessage(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public void sendServerMessage(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    @SuppressWarnings("unchecked")
    private static  <P extends CustomPacketPayload> void registerInternalC2S(PayloadRegistrar registrar, PacketHolder<P, ?> holder) {
        registrar.playToServer(holder.type(), (StreamCodec<? super RegistryFriendlyByteBuf, P>) holder.codec(), (payload, ctx) -> {
            if (holder.handler() != null) {
                ctx.enqueueWork(() -> holder.handler().handle(payload, ctx.player()));
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <P extends CustomPacketPayload> void registerInternalS2C(PayloadRegistrar registrar, PacketHolder<P, ?> holder) {
        registrar.playToClient(holder.type(), (StreamCodec<? super RegistryFriendlyByteBuf, P>) holder.codec(), (payload, ctx) -> {
            if (holder.handler() != null) {
                ctx.enqueueWork(() -> holder.handler().handle(payload, ctx.player()));
            }
        });
    }
}
