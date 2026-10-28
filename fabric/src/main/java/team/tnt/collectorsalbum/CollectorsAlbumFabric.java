package team.tnt.collectorsalbum;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import team.tnt.collectorsalbum.common.command.CollectorsAlbumCommand;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.init.SoundRegistry;
import team.tnt.collectorsalbum.common.resource.*;
import team.tnt.collectorsalbum.platform.FabricPlatform;

public class CollectorsAlbumFabric implements ModInitializer {

    public CollectorsAlbumFabric() {
        CollectorsAlbum.init();
    }

    @Override
    public void onInitialize() {
        FabricPlatform.bootstrap();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> FabricPlatform.server = server);
        ServerLifecycleEvents.SERVER_STOPPING.register(_ -> CollectorsAlbum.serverStopped());
        ServerLifecycleEvents.SERVER_STOPPED.register(_ -> FabricPlatform.server = null);
        UseItemCallback.EVENT.register(this::startPackOpening);
        CommandRegistrationCallback.EVENT.register(
                (commandDispatcher, _, _) -> CollectorsAlbumCommand.register(commandDispatcher)
        );

        ResourceLoader resourceLoader = ResourceLoader.get(PackType.SERVER_DATA);

        resourceLoader.addListenerOrdering(AlbumCardManager.IDENTIFIER, AlbumCategoryManager.IDENTIFIER);

        resourceLoader.registerReloadListener(AlbumCardManager.IDENTIFIER, AlbumCardManager.getInstance());
        resourceLoader.registerReloadListener(AlbumCategoryManager.IDENTIFIER, AlbumCategoryManager.getInstance());
        resourceLoader.registerReloadListener(AlbumBonusManager.IDENTIFIER, AlbumBonusManager.getInstance());
        resourceLoader.registerReloadListener(CardPackDropManager.IDENTIFIER, CardPackDropManager.getInstance());
        resourceLoader.registerReloadListener(MobAdditionalDropManager.IDENTIFIER, MobAdditionalDropManager.getInstance());

        CollectorsAlbum.NETWORK_MANAGER.bind();
    }

    private InteractionResult startPackOpening(Player player, Level level, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.has(ItemDataComponentRegistry.PACK_DROPS_TABLE.get())) {
            player.startUsingItem(hand);
            player.playSound(SoundRegistry.PACK_OPEN.get(), 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
