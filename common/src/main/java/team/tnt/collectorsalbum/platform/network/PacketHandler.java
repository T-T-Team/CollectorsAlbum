package team.tnt.collectorsalbum.platform.network;

import net.minecraft.world.entity.player.Player;

public interface PacketHandler<P extends NetworkMessage> {

    void handle(P payload, Player player);
}
