package team.tnt.collectorsalbum.config;

import dev.toma.configuration.config.Configurable;

public class PackDropConfig {

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment("Drop weight of common cards")
    public int commonCardDropChance;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment("Drop weight of uncommon cards")
    public int uncommonCardDropChance;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment("Drop weight of rare cards")
    public int rareCardDropChance;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment("Drop weight of epic cards")
    public int epicCardDropChance;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment("Drop weight of legendary cards")
    public int legendaryCardDropChance;

    @Configurable
    @Configurable.Range(min = 0)
    @Configurable.Comment("Drop weight of mythical cards")
    public int mythicalCardDropChance;

    public PackDropConfig(int commonCardDropChance, int uncommonCardDropChance, int rareCardDropChance, int epicCardDropChance, int legendaryCardDropChance, int mythicalCardDropChance) {
        this.commonCardDropChance = commonCardDropChance;
        this.uncommonCardDropChance = uncommonCardDropChance;
        this.rareCardDropChance = rareCardDropChance;
        this.epicCardDropChance = epicCardDropChance;
        this.legendaryCardDropChance = legendaryCardDropChance;
        this.mythicalCardDropChance = mythicalCardDropChance;
    }

    private int sum() {
        return commonCardDropChance + uncommonCardDropChance + rareCardDropChance + epicCardDropChance + legendaryCardDropChance + mythicalCardDropChance;
    }
}
