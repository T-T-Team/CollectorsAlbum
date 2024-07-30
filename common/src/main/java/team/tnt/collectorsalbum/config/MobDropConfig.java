package team.tnt.collectorsalbum.config;

import dev.toma.configuration.config.Configurable;

public final class MobDropConfig {

    @Configurable
    @Configurable.DecimalRange(min = 0.0, max = 1.0)
    @Configurable.Gui.NumberFormat("0.00")
    @Configurable.Gui.Slider
    @Configurable.Comment(value = "Chance that any card pack drop will be dropped", localize = true)
    public float mobPackDropChance = 0.1F;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment(value = "Common card pack drop weight", localize = true)
    public int commonPackDrop = 30;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment(value = "Uncommon card pack drop weight", localize = true)
    public int uncommonPackDrop = 25;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment(value = "Rare card pack drop weight", localize = true)
    public int rarePackDrop = 18;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment(value = "Epic card pack drop weight", localize = true)
    public int epicPackDrop = 13;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment(value = "Legendary card pack drop weight", localize = true)
    public int legendaryPackDrop = 10;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment(value = "Mythical card pack drop weight", localize = true)
    public int mythicalPackDrop = 4;
}
