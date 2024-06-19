package example.mod;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ExampleModConstants.MOD_ID)
public class ForgeExampleMod {

    public ForgeExampleMod() {
        ExampleMod.onConstruct();

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::onInit);
    }

    private void onInit(FMLCommonSetupEvent event) {
        ExampleMod.onInit();
    }
}
