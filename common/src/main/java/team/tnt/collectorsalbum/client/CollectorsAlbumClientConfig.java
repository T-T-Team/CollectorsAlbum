package team.tnt.collectorsalbum.client;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import team.tnt.collectorsalbum.CollectorsAlbum;

@Config(id = CollectorsAlbum.MOD_ID + "-client", group = CollectorsAlbum.MOD_ID)
public class CollectorsAlbumClientConfig {

    @Configurable
    @Configurable.Comment(value = "Allows you to skip animation of card pack opening", localize = true)
    public boolean packOpenAnimation = true;
}
