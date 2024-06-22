package team.tnt.collectorsalbum.platform.resource;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import team.tnt.collectorsalbum.platform.Identifiable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class FabricReloadListenerWrapper<T extends PreparableReloadListener & Identifiable> implements PreparableReloadListener, IdentifiableResourceReloadListener {

    private final T reloadListener;

    private FabricReloadListenerWrapper(T reloadListener) {
        this.reloadListener = reloadListener;
    }

    public static <T extends PreparableReloadListener & Identifiable> FabricReloadListenerWrapper<T> wrap(final T reloadListener) {
        Objects.requireNonNull(reloadListener);
        return new FabricReloadListenerWrapper<>(reloadListener);
    }

    @Override
    public ResourceLocation getFabricId() {
        return this.reloadListener.identifier();
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
        return this.reloadListener.reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
    }
}
