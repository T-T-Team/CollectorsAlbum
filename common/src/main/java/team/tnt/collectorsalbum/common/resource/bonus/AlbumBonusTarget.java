package team.tnt.collectorsalbum.common.resource.bonus;

public enum AlbumBonusTarget {

    GLOBAL,
    CATEGORY,
    UNIVERSAL;

    public boolean isGlobalBonus() {
        return this == UNIVERSAL || this == GLOBAL;
    }

    public boolean isCategoryBonus() {
        return this == UNIVERSAL || this == CATEGORY;
    }
}
