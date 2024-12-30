package team.tnt.collectorsalbum.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.tnt.collectorsalbum.CollectorsAlbum;

import java.util.List;

@Mixin(PlayerList.class)
public abstract class FabricPlayerListMixin {

    @Shadow public abstract List<ServerPlayer> getPlayers();

    @Inject(
            method = "placeNewPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;sendPlayerPermissionLevel(Lnet/minecraft/server/level/ServerPlayer;)V", shift = At.Shift.BY, by = -2)
    )
    private void collectorsAlbum$placeNewPlayer(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        CollectorsAlbum.sendPlayerDatapacks(player);
    }

    @Inject(
            method = "remove",
            at = @At("HEAD")
    )
    private void collectorsAlbum$playerLoggingOut(ServerPlayer player, CallbackInfo ci) {
        CollectorsAlbum.playerLoggedOut(player);
    }

    @Inject(
            method = "reloadResources",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V")
    )
    private void collectorsAlbum$onReloadResources(CallbackInfo ci) {
        this.getPlayers().forEach(CollectorsAlbum::sendPlayerDatapacks);
    }
}
