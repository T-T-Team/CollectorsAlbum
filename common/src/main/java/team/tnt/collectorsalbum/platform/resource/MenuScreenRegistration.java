package team.tnt.collectorsalbum.platform.resource;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public final class MenuScreenRegistration {

    private static List<Holder<?, ?>> holders = new ArrayList<>();

    public static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void registerScreenFactory(PlatformRegistry.Reference<MenuType<M>> ref, MenuScreens.ScreenConstructor<M, S> factory) {
        Holder<M, S> holder = new Holder<>(ref, factory);
        holders.add(holder);
    }

    public static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void bind() {
        MenuScreenRegistration.<M, S>bindRefs(MenuScreens::register);
    }

    @SuppressWarnings("unchecked")
    public static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void bindRefs(BiConsumer<MenuType<M>, MenuScreens.ScreenConstructor<M, S>> consumer) {
        for (Holder<?, ?> holder : holders) {
            Holder<M, S> typed = (Holder<M, S>) holder;
            consumer.accept(typed.referenceHolder().get(), typed.factory());
        }
        holders = null;
    }

    private record Holder<M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>>(PlatformRegistry.Reference<MenuType<M>> referenceHolder, MenuScreens.ScreenConstructor<M, S> factory) {}
}
