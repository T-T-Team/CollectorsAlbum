package team.tnt.collectoralbum.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.network.api.IPacket;

import java.util.function.Supplier;

public class SendAlbumBoostsPacket implements IPacket<SendAlbumBoostsPacket> {

    @Override
    public void encode(FriendlyByteBuf buffer) {
        CollectorsAlbum.ALBUM_CARD_BOOST_MANAGER.getBoosts()
                .ifPresent(boosts -> boosts.encode(buffer));
    }

    @Override
    public SendAlbumBoostsPacket decode(FriendlyByteBuf buffer) {
        CollectorsAlbum.ALBUM_CARD_BOOST_MANAGER.getBoosts()
                .ifPresent(boosts -> boosts.decode(buffer));
        return new SendAlbumBoostsPacket();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
    }
}
