package example.mod.services;

import example.mod.Platform;

public class NeoforgePlatform implements Platform {

    @Override
    public String getModLoaderName() {
        return "Neoforge";
    }
}
