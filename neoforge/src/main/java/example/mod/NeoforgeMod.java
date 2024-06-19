package example.mod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(ExampleModConstants.MOD_ID)
public class NeoforgeMod {

    public NeoforgeMod(IEventBus eventBus) {
        ExampleMod.onConstruct();

        eventBus.addListener(this::init);
    }

    private void init(FMLCommonSetupEvent event) {
        ExampleMod.onInit();
    }
}
