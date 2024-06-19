package example.mod.service;

import example.mod.Platform;

public class ForgePlatform implements Platform {

    @Override
    public String getModLoaderName() {
        return "Forge";
    }
}
