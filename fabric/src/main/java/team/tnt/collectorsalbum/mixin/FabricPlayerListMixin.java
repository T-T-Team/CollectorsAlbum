package team.tnt.collectorsalbum.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.tnt.collectorsalbum.CollectorsAlbum;

@Mixin(PlayerList.class)
public abstract class FabricPlayerListMixin {

    @Inject(
            method = "placeNewPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;sendPlayerPermissionLevel(Lnet/minecraft/server/level/ServerPlayer;)V", shift = At.Shift.BY, by = -2)
    )
    private void collectorsAlbum$placeNewPlayer(Connection connection, ServerPlayer player, CallbackInfo ci) {
        CollectorsAlbum.sendPlayerDatapacks(player);
    }

    @Inject(
            method = "remove",
            at = @At("HEAD")
    )
    private void collectorsAlbum$playerLoggingOut(ServerPlayer player, CallbackInfo ci) {
        CollectorsAlbum.playerLoggedOut(player);
    }
}
