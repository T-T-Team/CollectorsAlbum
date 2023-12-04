package team.tnt.collectoralbum.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.event.TickEvent;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.api.IAlbumScreenFactory;
import team.tnt.collectoralbum.client.screen.AlbumScreen;
import team.tnt.collectoralbum.common.ICardCategory;
import team.tnt.collectoralbum.common.init.MenuTypes;
import team.tnt.collectoralbum.common.menu.AlbumMenu;
import team.tnt.collectoralbum.data.boosts.OpType;

public final class CollectorsAlbumClient {

    private static final CollectorsAlbumClient INSTANCE = new CollectorsAlbumClient();
    private int timer;
    private int albumPageIndex;
    private int pageCount;

    public static CollectorsAlbumClient getClient() {
        return INSTANCE;
    }

    public void synchInit() {
        MenuScreens.register(MenuTypes.ALBUM.get(), (AlbumMenu menu, Inventory inventory, Component title) -> {
            ICardCategory category = menu.getCategory();
            if (category == null) {
                return new AlbumScreen(menu, inventory, title);
            }
            IAlbumScreenFactory factory = category.getAlbumScreenFactory();
            return factory.createAlbumScreen(menu, inventory, title);
        });
    }

    public void handleClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            return;
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            timer = 0;
            return;
        }
        ++timer;
        CollectorsAlbum.ALBUM_CARD_BOOST_MANAGER.getBoosts()
                .ifPresent(collection -> {
                    this.pageCount = collection.getActionsCount(OpType.ACTIVE);
                    if (this.pageCount > 0) {
                        this.albumPageIndex = (timer / 60) % this.pageCount;
                    }
                });
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getAlbumPageIndex() {
        return albumPageIndex;
    }
}