package team.tnt.collectoralbum.network.packet;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import team.tnt.collectoralbum.server.OpenCardPackContextHolder;
import team.tnt.collectoralbum.util.PlayerHelper;

import java.util.List;
import java.util.Optional;

public class RequestCardPackDropPacket extends AbstractNetworkHandlePacket<RequestCardPackDropPacket> {

    @Override
    public RequestCardPackDropPacket instantiate() {
        return new RequestCardPackDropPacket();
    }

    @Override
    protected void handlePacket(CustomPayloadEvent.Context context) {
        Optional<List<ItemStack>> optional = OpenCardPackContextHolder.getContextAndClear(context.getSender());
        optional.ifPresent(list -> {
            for (ItemStack stack : list) {
                PlayerHelper.giveItem(context.getSender(), stack);
            }
        });
    }
}
