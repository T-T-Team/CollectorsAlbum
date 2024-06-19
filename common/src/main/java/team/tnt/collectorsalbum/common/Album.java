package team.tnt.collectorsalbum.common;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.player.Player;

public class Album {

    private final Multimap<AlbumCategory, AlbumCard> cards = ArrayListMultimap.create();
    private int points;

    public void tick(Player player) {
        long timer = player.level().getGameTime();
    }
}
