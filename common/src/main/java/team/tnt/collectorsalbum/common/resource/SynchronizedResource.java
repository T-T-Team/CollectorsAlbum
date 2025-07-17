package team.tnt.collectorsalbum.common.resource;

import java.util.List;

public interface SynchronizedResource<T> {

    List<T> getDataForSync();

    void receiveNetworkData(List<T> data);
}
