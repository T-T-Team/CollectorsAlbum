package team.tnt.collectorsalbum.platform.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PacketHolder<P extends CustomPacketPayload, BUF extends FriendlyByteBuf>(Class<P> payloadType, CustomPacketPayload.Type<P> type, StreamCodec<BUF, P> codec, PacketHandler<P> handler) {
}
