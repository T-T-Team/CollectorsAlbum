package team.tnt.collectorsalbum.platform.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import team.tnt.collectorsalbum.platform.Side;

import java.util.List;

public interface Network {

    void initialize(ResourceLocation identifier, Side side, List<PacketHolder<?, ?>> c2s, List<PacketHolder<?, ?>> s2c);

    void sendClientMessage(ServerPlayer player, NetworkMessage payload);

    void sendServerMessage(NetworkMessage payload);
}
