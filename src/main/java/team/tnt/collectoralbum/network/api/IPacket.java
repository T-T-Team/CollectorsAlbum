package team.tnt.collectoralbum.network.api;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public interface IPacket<T> {

    void encode(FriendlyByteBuf buffer);

    T decode(FriendlyByteBuf buffer);

    void handle(CustomPayloadEvent.Context supplier);
}
