package team.tnt.collectorsalbum.config;

import dev.toma.configuration.config.Configurable;

public final class MobDropConfig {

    @Configurable
    @Configurable.DecimalRange(min = 0.0, max = 1.0)
    @Configurable.Gui.NumberFormat("0.00###")
    @Configurable.Comment("Chance that any card pack drop will be dropped")
    public float mobPackDropChance = 0.1F;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment("Common card pack drop weight")
    public int commonPackDrop = 30;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment("Uncommon card pack drop weight")
    public int uncommonPackDrop = 25;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment("Rare card pack drop weight")
    public int rarePackDrop = 18;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment("Epic card pack drop weight")
    public int epicPackDrop = 13;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment("Legendary card pack drop weight")
    public int legendaryPackDrop = 10;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment("Mythical card pack drop weight")
    public int mythicalPackDrop = 4;
}
