package team.tnt.collectorsalbum.platform.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface NetworkMessage {

    ResourceLocation getPacketId();

    void write(FriendlyByteBuf buf);

    void handle(Player player);
}
