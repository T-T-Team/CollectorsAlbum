package team.tnt.collectorsalbum.common;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface AlbumCategory {

    ResourceLocation identifier();

    Component getDisplayText();

    boolean accepts(AlbumCard card);

    Optional<AlbumCard> getCardAt(int index);
}
