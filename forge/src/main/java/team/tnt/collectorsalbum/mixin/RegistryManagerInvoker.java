package team.tnt.collectorsalbum.mixin;

import net.minecraft.core.Registry;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RegistryManager.class)
public interface RegistryManagerInvoker {

    @Invoker("injectForgeRegistry")
    static <T> void tarkovCraft$invokeInjectForgeRegistry(ForgeRegistry<T> registry, Registry<? extends Registry<?>> root) {
        throw new AssertionError();
    }
}
