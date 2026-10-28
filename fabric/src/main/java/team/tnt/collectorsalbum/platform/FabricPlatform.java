package team.tnt.collectorsalbum.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.Nullable;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.platform.registration.FabricRegistrationManager;
import team.tnt.collectorsalbum.platform.registration.RegistrationManager;

import java.util.ArrayList;
import java.util.List;

public class FabricPlatform implements Platform {

    public static MinecraftServer server;
    private static List<FabricRegistrationManager<?>> registries = new ArrayList<>();

    @Override
    public <T> RegistrationManager<T> createRegistryManager(Registry<T> registry) {
        var fabricRegistry = new FabricRegistrationManager<>(registry, CollectorsAlbum.MOD_ID);
        registries.add(fabricRegistry);
        return fabricRegistry;
    }

    @Override
    public <T> Registry<T> createRegistry(String namespace, String name) {
        ResourceKey<Registry<T>> key = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(namespace, name));
        return FabricRegistryBuilder.create(key).buildAndRegister();
    }

    @Override
    public Side getSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? Side.CLIENT : Side.SERVER;
    }

    @Override
    public boolean isModLoaded(String namespace) {
        return FabricLoader.getInstance().isModLoaded(namespace);
    }

    @Override
    public MinecraftServer getServerInstance() {
        return server;
    }

    @Override
    public <T> void openMenu(ServerPlayer player, StreamCodec<? super FriendlyByteBuf, T> codec, PlatformMenuProvider<T> provider) {
        player.openMenu(new ExtendedMenuProvider<>() {
            @Override
            public Object getScreenOpeningData(ServerPlayer player) {
                return provider.getMenuData(player);
            }

            @Override
            public Component getDisplayName() {
                return provider.getTitle();
            }

            @Override
            public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return provider.createMenu(containerId, inventory, player);
            }
        });
    }

    @Override
    public <M extends AbstractContainerMenu, D> MenuType<M> createMenu(MenuFactory<M, D> factory, StreamCodec<? super FriendlyByteBuf, D> dataCodec) {
        return new ExtendedMenuType<>(factory::createMenu, dataCodec);
    }

    public static void bootstrap() {
        registries.forEach(FabricRegistrationManager::bindRegistryElements);
    }
}
