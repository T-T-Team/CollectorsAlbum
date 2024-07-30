package team.tnt.collectorsalbum.config;

import dev.toma.configuration.config.Configurable;

public class AlbumBonusConfig {

    @Configurable
    @Configurable.Comment(value = "Allows you to disable the default health boost effect", localize = true)
    public boolean healthBonusEnabled = true;

    @Configurable
    @Configurable.Comment(value = "Allows you to disable the default 'Tools' category haste effect", localize = true)
    public boolean hasteBonusEnabled = true;

    @Configurable
    @Configurable.Comment(value = "Allows you to disable the default 'Armor' category resistance effect", localize = true)
    public boolean resistanceBonusEnabled = true;

    @Configurable
    @Configurable.Comment(value = "Allows you to disable the default 'Mob' category strength effect", localize = true)
    public boolean strengthBonusEnabled = true;

    @Configurable
    @Configurable.Comment(value = "Allows you to disable the default 'Nature' category regeneration effect", localize = true)
    public boolean regenerationBonusEnabled = true;

    @Configurable
    @Configurable.Comment(value = "Allows you to disable the default 'Items' category speed effect", localize = true)
    public boolean speedBonusEnabled = true;

    @Configurable
    @Configurable.FixedSize
    @Configurable.Range(min = 0, max = 1024)
    @Configurable.Comment(value = {"Allows you to configure health boost steps given from album points", "1 = 0.5 hearts"}, localize = true)
    public int[] healthBonusLevelSteps = { 4, 8, 12, 16, 20, 40 };

    @Configurable
    @Configurable.Range(min = 0, max = 255)
    @Configurable.Comment(value = "Default effect amplifier given when collecting all cards within single category", localize = true)
    public int baseMobEffectAmplifier = 0;

    @Configurable
    @Configurable.Range(min = 0, max = 255)
    @Configurable.Comment(value = "Default effect amplifier given when collecting all cards within single category with Mythical rarity",localize = true)
    public int fullMobEffectAmplifier = 1;
}
