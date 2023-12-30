package team.tnt.collectoralbum.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.common.init.ItemRegistry;

public final class CreativeTabs {

    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CollectorsAlbum.MODID);

    public static final RegistryObject<CreativeModeTab> MOD_TAB = REGISTER.register("collectorsalbum.tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ItemRegistry.ALBUM.get()))
            .title(Component.translatable("itemGroup.collectorsalbum.tab"))
            .displayItems((parameters, output) -> {
                output.accept(ItemRegistry.ALBUM.get());
                ForgeRegistries.ITEMS.getValues().stream()
                        .filter(item -> ForgeRegistries.ITEMS.getKey(item).getNamespace().equals(CollectorsAlbum.MODID))
                        .forEach(output::accept);
            })
            .build()
    );
}
