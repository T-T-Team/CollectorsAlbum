package team.tnt.collectorsalbum.platform.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PacketHolder<P extends CustomPacketPayload>(Class<P> payloadType, CustomPacketPayload.Type<P> type, StreamCodec<FriendlyByteBuf, P> codec, PacketHandler<P> handler) {
}
