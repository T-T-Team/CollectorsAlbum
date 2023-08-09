package team.tnt.collectoralbum.config;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import team.tnt.collectoralbum.CollectorsAlbum;

@Config(id = CollectorsAlbum.MODID)
public class ModConfig {

    @Configurable
    @Configurable.Comment("Configure card package drop chances from mobs")
    public MobDropConfig mobDrops = new MobDropConfig();

    @Configurable
    @Configurable.Comment("Keep album in inventory through death")
    public boolean persistAlbumThroughDeath = true;

    @Configurable
    @Configurable.Comment("When set to true no cards will be received from package unless you flip all cards")
    public boolean requireTurnAllCards = false;

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment("When allowed, all unpacked cards will be moved to your album if applicable")
    public boolean autoEquipUnpackedCards = true;
}
