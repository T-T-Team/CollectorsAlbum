package team.tnt.collectorsalbum.platform.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public interface PacketHandler<P extends CustomPacketPayload> {

    void handle(P payload, Player player);
}
