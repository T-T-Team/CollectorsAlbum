package team.tnt.collectoralbum.common;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import team.tnt.collectoralbum.CollectorsAlbum;
import team.tnt.collectoralbum.common.init.ItemRegistry;
import team.tnt.collectoralbum.common.item.CardItem;
import team.tnt.collectoralbum.common.item.CardPackItem;

@Mod.EventBusSubscriber(modid = CollectorsAlbum.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CreativeTabs {

    @SubscribeEvent
    public static void registerCreativeTabs(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(CollectorsAlbum.MODID, "tab"), cfg -> cfg.title(Component.translatable("itemGroup.collectorsalbum.tab"))
                .icon(() -> new ItemStack(ItemRegistry.ALBUM.get()))
                .displayItems((params, output) -> {
                    output.accept(ItemRegistry.ALBUM.get());
                    ForgeRegistries.ITEMS.getValues().stream()
                            .filter(item -> item instanceof CardPackItem || item instanceof CardItem)
                            .forEach(output::accept);
                })
                .build());
    }
}
