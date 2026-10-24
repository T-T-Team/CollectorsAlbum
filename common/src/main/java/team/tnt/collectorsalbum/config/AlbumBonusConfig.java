package team.tnt.collectorsalbum.config;

import dev.toma.configuration.config.Configurable;

public class AlbumBonusConfig {

    @Configurable
    @Configurable.Comment("Allows you to disable the default health boost effect")
    @Configurable.Synchronized
    public boolean healthBonusEnabled = true;

    @Configurable
    @Configurable.Comment("Allows you to disable the default 'Tools' category haste effect")
    @Configurable.Synchronized
    public boolean hasteBonusEnabled = true;

    @Configurable
    @Configurable.Comment("Allows you to disable the default 'Armor' category resistance effect")
    @Configurable.Synchronized
    public boolean resistanceBonusEnabled = true;

    @Configurable
    @Configurable.Comment("Allows you to disable the default 'Mob' category strength effect")
    @Configurable.Synchronized
    public boolean strengthBonusEnabled = true;

    @Configurable
    @Configurable.Comment("Allows you to disable the default 'Nature' category regeneration effect")
    @Configurable.Synchronized
    public boolean regenerationBonusEnabled = true;

    @Configurable
    @Configurable.Comment("Allows you to disable the default 'Items' category speed effect")
    @Configurable.Synchronized
    public boolean speedBonusEnabled = true;

    @Configurable
    @Configurable.FixedSize
    @Configurable.Range(min = 0, max = 1024)
    @Configurable.Comment({"Allows you to configure health boost steps given from album points", "1 = 0.5 hearts"})
    public int[] healthBonusLevelSteps = { 4, 8, 12, 16, 20, 40 };

    @Configurable
    @Configurable.Range(min = 0, max = 255)
    @Configurable.Comment("Default effect amplifier given when collecting all cards within single category")
    public int baseMobEffectAmplifier = 0;

    @Configurable
    @Configurable.Range(min = 0, max = 255)
    @Configurable.Comment("Default effect amplifier given when collecting all cards within single category with Mythical rarity")
    public int fullMobEffectAmplifier = 1;
}
