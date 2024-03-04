package team.tnt.collectoralbum.network.packet;

import net.minecraftforge.network.NetworkEvent;
import team.tnt.collectoralbum.network.api.IPacket;

import java.util.function.Supplier;

public abstract class AbstractNetworkPacket<T> implements IPacket<T> {

    protected abstract void handlePacket(NetworkEvent.Context context);

    @Override
    public final void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> this.handlePacket(context));
        context.setPacketHandled(true);
    }
}
