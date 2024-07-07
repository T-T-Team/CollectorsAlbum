package team.tnt.collectorsalbum.common;

public enum AlbumLocateState {

    NOT_FOUND,
    FOUND;

    public boolean exists() {
        return this == FOUND;
    }
}
