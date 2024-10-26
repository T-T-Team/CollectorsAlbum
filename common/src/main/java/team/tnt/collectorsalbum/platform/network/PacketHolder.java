package team.tnt.collectorsalbum.platform.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public record PacketHolder<P extends NetworkMessage, BUF extends FriendlyByteBuf>(ResourceLocation pid, Class<P> payloadType, Function<FriendlyByteBuf, P> decoder) {
}
