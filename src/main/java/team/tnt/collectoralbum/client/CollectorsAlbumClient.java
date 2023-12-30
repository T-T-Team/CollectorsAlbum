package team.tnt.collectoralbum.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.data.boosts.OpType;

public final class CollectorsAlbumClient {

    private static final CollectorsAlbumClient INSTANCE = new CollectorsAlbumClient();
    private int timer;
    private int albumPageIndex;
    private int pageCount;

    public static CollectorsAlbumClient getClient() {
        return INSTANCE;
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