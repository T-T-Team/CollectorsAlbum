package team.tnt.collectorsalbum.network;

import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.platform.network.PacketDirection;

public final class NetworkManager {

    public static void init() {
        CollectorsAlbum.NETWORK_MANAGER.registerPacket(PacketDirection.SERVER_TO_CLIENT, S2C_OpenCardPackScreen.IDENTIFIER, S2C_OpenCardPackScreen.class, S2C_OpenCardPackScreen::read);
        CollectorsAlbum.NETWORK_MANAGER.registerPacket(PacketDirection.SERVER_TO_CLIENT, S2C_SendDatapackResources.IDENTIFIER, S2C_SendDatapackResources.class, S2C_SendDatapackResources::read);

        CollectorsAlbum.NETWORK_MANAGER.registerPacket(PacketDirection.CLIENT_TO_SERVER, C2S_CompleteOpeningCardPack.IDENTIFIER, C2S_CompleteOpeningCardPack.class, C2S_CompleteOpeningCardPack::read);
        CollectorsAlbum.NETWORK_MANAGER.registerPacket(PacketDirection.CLIENT_TO_SERVER, C2S_RequestAlbumCategoryInventory.IDENTIFIER, C2S_RequestAlbumCategoryInventory.class, C2S_RequestAlbumCategoryInventory::read);
    }
}
