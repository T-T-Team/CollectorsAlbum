package team.tnt.collectorsalbum.network;

import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.platform.network.PacketDirection;

public final class NetworkManager {

    public static void init() {
        CollectorsAlbum.NETWORK_MANAGER.registerPacket(PacketDirection.SERVER_TO_CLIENT, S2C_OpenCardPackScreen.class, S2C_OpenCardPackScreen.TYPE, S2C_OpenCardPackScreen.CODEC, S2C_OpenCardPackScreen::onPacketReceived);

        CollectorsAlbum.NETWORK_MANAGER.registerPacket(PacketDirection.CLIENT_TO_SERVER, C2S_CompleteOpeningCardPack.class, C2S_CompleteOpeningCardPack.TYPE, C2S_CompleteOpeningCardPack.CODEC, C2S_CompleteOpeningCardPack::onPacketReceived);
    }
}
