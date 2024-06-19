package example.mod;

import net.fabricmc.api.ModInitializer;

public class FabricExampleMod implements ModInitializer {

    public FabricExampleMod() {
        ExampleMod.onConstruct();
    }

    @Override
    public void onInitialize() {
        ExampleMod.onInit();
        ExampleModConstants.LOGGER.info("Fabric example mod initialized");
    }
}
