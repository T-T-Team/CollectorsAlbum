package team.tnt.collectorsalbum.config;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import team.tnt.collectorsalbum.CollectorsAlbum;

@Config(id = CollectorsAlbum.MOD_ID)
public final class CollectorsAlbumConfig {

    @Configurable
    @Configurable.Comment({"Configurations related to card package drop distributions",
            "Keep in mind to actually change contents of the pack you need to make custom datapack",
            "See wiki for that"})
    public DefaultPackDropsConfig packDrops = new DefaultPackDropsConfig();

    @Configurable
    @Configurable.Comment({"Configuration related to mob drops",
            "Keep in mind to actually change contents of the pack you need to make custom datapack",
            "See wiki for that"
    })
    public MobDropConfig mobDrops = new MobDropConfig();
}
