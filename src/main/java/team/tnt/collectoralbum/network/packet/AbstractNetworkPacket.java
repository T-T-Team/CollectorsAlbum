package team.tnt.collectoralbum.network.packet;

import net.minecraftforge.event.network.CustomPayloadEvent;
import team.tnt.collectoralbum.network.api.IPacket;

public abstract class AbstractNetworkPacket<T> implements IPacket<T> {

    protected abstract void handlePacket(CustomPayloadEvent.Context context);

    @Override
    public final void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> this.handlePacket(context));
        context.setPacketHandled(true);
    }
}
