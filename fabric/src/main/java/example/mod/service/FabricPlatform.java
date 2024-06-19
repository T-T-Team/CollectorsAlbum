package example.mod.service;

import example.mod.Platform;

public class FabricPlatform implements Platform {

    @Override
    public String getModLoaderName() {
        return "Fabric";
    }
}
