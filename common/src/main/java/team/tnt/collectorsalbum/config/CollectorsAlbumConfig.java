package team.tnt.collectorsalbum.config;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.UpdateRestrictions;
import team.tnt.collectorsalbum.CollectorsAlbum;

@Config(id = CollectorsAlbum.MOD_ID)
public final class CollectorsAlbumConfig {

    @Configurable
    @Configurable.UpdateRestriction(UpdateRestrictions.MAIN_MENU)
    @Configurable.Comment(value = {"Configuration related to card package drop distributions",
            "Keep in mind to actually change contents of the pack you need to make custom datapack",
            "See wiki for that"}, localize = true)
    public DefaultPackDropsConfig packDrops = new DefaultPackDropsConfig();

    @Configurable
    @Configurable.UpdateRestriction(UpdateRestrictions.MAIN_MENU)
    @Configurable.Comment(value = {"Configuration related to mob drops",
            "Keep in mind to actually change contents of the pack you need to make custom datapack",
            "See wiki for that"
    }, localize = true)
    public MobDropConfig mobDrops = new MobDropConfig();

    @Configurable
    @Configurable.UpdateRestriction(UpdateRestrictions.MAIN_MENU)
    @Configurable.Comment(value = {"Configuration related to album bonuses",
            "Keep in mind to actually change the bonuses to something you need to make custom datapack",
            "See wiki for that"
    }, localize = true)
    public AlbumBonusConfig bonusConfig = new AlbumBonusConfig();
}
