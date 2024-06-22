package team.tnt.collectorsalbum.platform.network;

import team.tnt.collectorsalbum.platform.Side;

public enum PacketDirection {

    CLIENT_TO_SERVER(Side.SERVER),
    SERVER_TO_CLIENT(Side.CLIENT);

    private final Side receivingSide;

    PacketDirection(Side receivingSide) {
        this.receivingSide = receivingSide;
    }
}
