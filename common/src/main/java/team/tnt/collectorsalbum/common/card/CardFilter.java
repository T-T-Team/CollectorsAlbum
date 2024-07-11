package team.tnt.collectorsalbum.common.card;

import java.util.Set;

public interface CardFilter {

    Set<CardRarity> rarities();

    IntFilter numberFilter();

    IntFilter pointFilter();
}
