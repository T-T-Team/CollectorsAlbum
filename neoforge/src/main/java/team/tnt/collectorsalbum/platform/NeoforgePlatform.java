package team.tnt.collectorsalbum.platform;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.platform.registration.NeoForgeRegistrationManager;
import team.tnt.collectorsalbum.platform.registration.RegistrationManager;

import java.util.HashMap;
import java.util.Map;

public class NeoforgePlatform implements Platform {

    private static final Map<ResourceKey<? extends Registry<?>>, NeoForgeRegistrationManager<?>> REGISTRIES = new HashMap<>();

    @Override
    public <T> RegistrationManager<T> createRegistryManager(Registry<T> registry) {
        NeoForgeRegistrationManager<T> manager = new NeoForgeRegistrationManager<>(CollectorsAlbum.MOD_ID);
        REGISTRIES.put(registry.key(), manager);
        return manager;
    }

    @Override
    public <T> Registry<T> createRegistry(String namespace, String name) {
        ResourceKey<Registry<T>> key = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(namespace, name));
        return new RegistryBuilder<>(key)
                .create();
    }

    @Override
    public Side getSide() {
        return FMLEnvironment.getDist() == Dist.CLIENT ? Side.CLIENT : Side.SERVER;
    }

    @Override
    public boolean isModLoaded(String namespace) {
        return ModList.get().isLoaded(namespace);
    }

    @Override
    public MinecraftServer getServerInstance() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public <T> void openMenu(ServerPlayer player, StreamCodec<? super FriendlyByteBuf, T> codec, PlatformMenuProvider<T> provider) {
        player.openMenu(new SimpleMenuProvider(
                provider::createMenu,
                provider.getTitle()
        ), buf -> {
            T data = provider.getMenuData(player);
            codec.encode(buf, data);
        });
    }

    @Override
    public <M extends AbstractContainerMenu, D> MenuType<M> createMenu(MenuFactory<M, D> factory, StreamCodec<? super FriendlyByteBuf, D> dataCodec) {
        return IMenuTypeExtension.create((id, inv, buf) -> {
            D data = dataCodec.decode(buf);
            return factory.createMenu(id, inv, data);
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> void bootstrapRegistries(RegisterEvent event) {
        ResourceKey<? extends Registry<T>> key = (ResourceKey<? extends Registry<T>>) event.getRegistryKey();
        NeoForgeRegistrationManager<T> manager = (NeoForgeRegistrationManager<T>) REGISTRIES.get(key);
        if (manager != null) {
            event.register(key, manager::bind);
        }
    }
}
