package team.tnt.collectorsalbum.platform.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import team.tnt.collectorsalbum.platform.Platform;

import java.util.List;

public interface Network {

    void initialize(ResourceLocation identifier, List<PacketHolder<?, ?>> c2s, List<PacketHolder<?, ?>> s2c);

    void sendClientMessage(ServerPlayer player, CustomPacketPayload payload);

    void sendServerMessage(CustomPacketPayload payload);

    default void sendAllClientMessage(CustomPacketPayload payload) {
        MinecraftServer server = Platform.INSTANCE.getServerInstance();
        if (server != null) {
            PlayerList playerList = server.getPlayerList();
            playerList.getPlayers().forEach(player -> sendClientMessage(player, payload));
        }
    }
}
